package imt.fil.a3.recherche.fj.v2;

import imt.fil.a3.recherche.fj.FJUtils;
import imt.fil.a3.recherche.fj.parser.*;
import imt.fil.a3.recherche.fj.parser.expression.*;
import imt.fil.a3.recherche.fj.parser.type.*;

import java.util.*;
import java.util.stream.Stream;

public final class FJInterpreter {

    final HashMap<String, FJType> classTable;

    public FJInterpreter(HashMap<String, FJType> classTable) {
        this.classTable = classTable;
    }

    /**
     * Evaluates an expression.
     * @param expr An expression.
     * @return An expression after processing one reduction step.
     */
    private Optional<FJExpr> _eval(FJExpr expr) {
        if (expr instanceof final FJCreateObject createObject) { // RC-New-Arg
            final List<FJExpr> args = createObject.args.stream().map(this::_eval)
                .flatMap(Optional::stream).toList();
            return Optional.of(new FJCreateObject(createObject.className, args));
        } else if (expr instanceof final FJFieldAccess fieldAccess) { // R-Field
            if (fieldAccess.object.isValue()) {
                if (fieldAccess.object instanceof final FJCreateObject createObject) {
                    final Optional<List<FJField>> _fields =
                        FJUtils.classFields(this.classTable, createObject.className);
                    if (_fields.isEmpty()) return Optional.empty();
                    final List<FJField> fields = _fields.get();

                    Optional<Integer> index = Optional.empty();
                    for (int i = 0; i < fields.size(); i++) {
                        if (fields.get(i).name.equals(fieldAccess.fieldName)) {
                            index = Optional.of(i);
                            break;
                        }
                    }
                    if (index.isEmpty()) return Optional.empty();

                    return Optional.of(createObject.args.get(index.get()).lambdaMark(fields.get(index.get()).name));
                } else {
                    return Optional.empty(); // Not an object instance
                }
            } else { // RC-Field
                return this._eval(expr).map(e -> new FJFieldAccess(e, fieldAccess.fieldName));
            }
        } else if (expr instanceof final FJMethodInvocation methodInvocation) {
            if (methodInvocation.source.isValue()) {
                if (methodInvocation.args.stream().allMatch(FJExpr::isValue)) {
                    if (methodInvocation.source instanceof final FJCreateObject createObject) { // R-Invk
                        final Optional<FJMethodTypeSignature> methodType = FJUtils.methodType(
                            this.classTable,
                            methodInvocation.methodName,
                            createObject.className
                        );
                        if (methodType.isEmpty()) { return Optional.empty(); } // No method type

                        final Optional<FJMethodBodySignature> methodBody = FJUtils.methodBody(
                            this.classTable,
                            methodInvocation.methodName,
                            createObject.className
                        );
                        if (methodBody.isEmpty()) { return Optional.empty(); } // No method body

                        final List<FJExpr> args = Collections.emptyList();
                        // <=> zip(methodInvocation.args, methodType.parameterTypeNames)
                        for (int i = 0; i < methodInvocation.args.size(); i++) {
                            args.add(methodInvocation.args.get(i)
                                .lambdaMark(methodType.get().parameterTypeNames.get(i)));
                        }
                        args.add(methodInvocation.source);
                        final List<String> parameterNames = Stream.concat(
                            methodBody.get().argumentNames.stream(),
                            Stream.of("this")
                        ).toList();
                        return this.substitute(
                            parameterNames,
                            args,
                            methodBody.get().body.lambdaMark(methodType.get().returnTypeName)
                        );
                    } else if (
                        methodInvocation.source instanceof final FJCast fjCast
                            && fjCast.body instanceof final FJLambda lambda
                    ) {
                        final Optional<FJMethodTypeSignature> methodType = FJUtils.methodType(
                            this.classTable,
                            methodInvocation.methodName,
                            fjCast.typeName
                        );
                        if (methodType.isEmpty()) { return Optional.empty(); } // No method type
                        final List<FJExpr> args = Collections.emptyList();
                        // <=> zip(methodInvocation.args, methodType.parameterTypeNames)
                        for (int i = 0; i < methodInvocation.args.size(); i++) {
                            args.add(methodInvocation.args.get(i)
                                .lambdaMark(methodType.get().parameterTypeNames.get(i)));
                        }

                        final Optional<FJMethodBodySignature> methodBody = FJUtils.methodBody(
                            this.classTable,
                            methodInvocation.methodName,
                            fjCast.typeName
                        );
                        if (methodBody.isPresent()) { // R-Default
                            return substitute(
                                methodBody.get().argumentNames,
                                args,
                                methodBody.get().body.lambdaMark(methodType.get().returnTypeName)
                            );
                        } else { // R-Lam
                            return this.substitute(
                                lambda.args.stream().map(a -> a.name).toList(),
                                args,
                                lambda.body.lambdaMark(methodType.get().returnTypeName)
                            );
                        }
                    } else {
                        return Optional.empty();
                    }
                } else { // RC-Invk-Arg
                    final List<FJExpr> args = methodInvocation.args.stream().map(this::_eval)
                        .flatMap(Optional::stream).toList();
                    return Optional.of(new FJMethodInvocation(
                        methodInvocation.source,
                        methodInvocation.methodName,
                        args
                    ));
                }
            } else { // RC-Invk-Recv
                return this._eval(methodInvocation.source)
                    .map(e -> new FJMethodInvocation(e, methodInvocation.methodName, methodInvocation.args));
            }
        } else if (expr instanceof final FJCast fjCast) {
            if (fjCast.body.isValue()) {
                if (fjCast.body instanceof final FJCreateObject createObject) {
                    if (FJUtils.isSubtype(this.classTable, createObject.className, fjCast.typeName)) { // R-Cast
                        return Optional.of(fjCast.body);
                    } else {
                        return Optional.empty();
                    }
                } else if (fjCast.body instanceof final FJCast fjCast2 && fjCast2.body instanceof FJLambda) {
                    if (FJUtils.isSubtype(this.classTable, fjCast2.typeName, fjCast.typeName)) { // R-Cast-Lam
                        return Optional.of(fjCast.body);
                    } else {
                        return Optional.empty();
                    }
                } else {
                    return Optional.empty();
                }
            } else { // RC-Cast
                return this._eval(fjCast.body).map(e -> new FJCast(fjCast.typeName, e));
            }
        } else if (expr instanceof FJLambda) {
            return Optional.of(expr);
        } else if (expr instanceof FJVariable) {
            return Optional.empty();
        } else {
            throw new RuntimeException("Unexpected code path: expression type not supported.");
        }
    }

    /**
     * Evaluates an expression recursively.
     * @return A value after all the reduction steps.
     */
    public FJExpr eval(FJExpr expr) {
        if (expr.isValue()) {
            return expr;
        } else {
            return eval(_eval(expr).orElse(expr));
        }
    }

    /**
     * Replaces actual parameters in method body expression.
     * @return A new changed expression.
     */
    private Optional<FJExpr> substitute(List<String> parameterNames, List<FJExpr> args, FJExpr body) {
        if (body instanceof final FJVariable fjVariable) {
            final int index = parameterNames.indexOf(fjVariable.name);
            if (index >= 0 && args.size() > index) {
                return Optional.of(args.get(index));
            } else {
                return Optional.empty();
            }
        } else if (body instanceof final FJFieldAccess fieldAccess) {
            return this.substitute(parameterNames, args, fieldAccess.object)
                .map(e -> new FJFieldAccess(e, fieldAccess.fieldName));
        } else if (body instanceof final FJMethodInvocation methodInvocation) {
            return this.substitute(parameterNames, args, methodInvocation.source)
                .map(e -> {
                    final List<FJExpr> _args = methodInvocation.args.stream()
                        .map(a -> this.substitute(parameterNames, args, a))
                        .flatMap(Optional::stream).toList();
                    return new FJMethodInvocation(e, methodInvocation.methodName, _args);
                });
        } else if (body instanceof final FJCreateObject createObject) {
            final List<FJExpr> _args = createObject.args.stream()
                .map(a -> this.substitute(parameterNames, args, a))
                .flatMap(Optional::stream).toList();
            return Optional.of(new FJCreateObject(createObject.className, _args));
        } else if (body instanceof final FJCast fjCast) {
            return this.substitute(parameterNames, args, fjCast.body)
                .map(e -> new FJCast(fjCast.typeName, e));
        } else if (body instanceof FJLambda) {
            return Optional.of(body); // Do nothing
        } else {
            throw new RuntimeException("Unexpected code path: expression type not supported.");
        }
    }

}
