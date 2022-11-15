package imt.fil.a3.recherche.fj.model.java.expression;

import imt.fil.a3.recherche.fj.model.error.MethodNotFound;
import imt.fil.a3.recherche.fj.model.error.ParamsTypeMismatch;
import imt.fil.a3.recherche.fj.model.error.TypeError;
import imt.fil.a3.recherche.fj.model.java.type.FJType;
import imt.fil.a3.recherche.fj.model.misc.MethodTypeSignature;
import imt.fil.a3.recherche.fj.model.misc.TypeMismatch;
import imt.fil.a3.recherche.fj.util.FJUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public record FJMethodInvocation(
    FJExpr source,
    String methodName,
    List<FJExpr> args
) implements FJExpr {
    @Override
    public String getTypeName(
        final HashMap<String, FJType> classTable,
        final HashMap<String, String> context
    ) throws TypeError { // T-Invk
        final String typeName = this.source.getTypeName(classTable, context);

        final Optional<MethodTypeSignature> methodTypeSignature =
            FJUtils.methodType(classTable, this.methodName, typeName);
        if (methodTypeSignature.isEmpty()) throw new MethodNotFound(this.methodName, typeName);
        final List<String> parameterTypes = methodTypeSignature.get().parameterTypeNames();

        if (this.args.size() != parameterTypes.size()) throw new ParamsTypeMismatch(new ArrayList<>());

        var temp = new ArrayList<TypeMismatch>();
        for (int i = 0; i < this.args.size(); i++) {
            final FJExpr arg = this.args.get(i);
            final String type = parameterTypes.get(i);
            temp.add(new TypeMismatch(arg.lambdaMark(type), type));
        }

        // Check method invocation arguments typing
        for (final TypeMismatch tm : temp) {
            final String type;
            try {
                type = tm.expression().getTypeName(classTable, context);
            } catch (TypeError e) {
                throw new ParamsTypeMismatch(temp);
            }
            if (!FJUtils.isSubtype(classTable, type, tm.expectedTypeName())) {
                throw new ParamsTypeMismatch(temp);
            }
        }

        // Method invocation is correctly typed
        return methodTypeSignature.get().returnTypeName();
    }

    @Override
    public FJMethodInvocation removingRuntimeAnnotation() {
        return new FJMethodInvocation(
            this.source.removingRuntimeAnnotation(),
            this.methodName,
            this.args.stream().map(FJExpr::removingRuntimeAnnotation).toList()
        );
    }

    @Override
    public Boolean isValue() { return false; }

    @Override
    public Optional<FJExpr> _eval(final HashMap<String, FJType> classTable) {
        // If `this.source` has not been evaluated, evaluate it (recursivity).
        if (!this.source.isValue()) { // RC-Invk-Recv
            return this.source._eval(classTable)
                .map(e -> new FJMethodInvocation(e, this.methodName, this.args));
        }

        // If some arguments have not been evaluated, evaluate them and recursively evaluate the expression.
        if (!this.args.stream().allMatch(FJExpr::isValue)) { // RC-Invk-Arg
            final List<FJExpr> args = this.args.stream().map(e -> e._eval(classTable))
                .flatMap(Optional::stream).toList();
            return Optional.of(new FJMethodInvocation(this.source, this.methodName, args).eval(classTable));
        }

        return this.source.evalMethodInvocation(classTable, this);
    }

    @Override
    public Optional<FJExpr> substitute(List<String> parameterNames, List<FJExpr> args) {
        return this.source.substitute(parameterNames, args)
            .map(e -> {
                final List<FJExpr> _args = this.args.stream()
                    .map(a -> a.substitute(parameterNames, args))
                    .flatMap(Optional::stream).toList();
                return new FJMethodInvocation(e, this.methodName, _args);
            });
    }
}
