package imt.fil.a3.recherche.fj.model.java.expression;

import imt.fil.a3.recherche.fj.model.TypeCheckingContext;
import imt.fil.a3.recherche.fj.model.TypeTable;
import imt.fil.a3.recherche.fj.model.error.*;
import imt.fil.a3.recherche.fj.model.misc.MethodTypeSignature;
import imt.fil.a3.recherche.fj.model.misc.TypeAnnotatedExpression;
import imt.fil.a3.recherche.fj.model.misc.TypeMismatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record FJMethodInvocation(
    FJExpr source,
    String methodName,
    List<FJExpr> args
) implements FJExpr {
    @Override
    public TypeAnnotatedExpression getTypeApproach1(final TypeCheckingContext context) throws TypeError { // T-Invk
        // Get type of expression on which we want to invoke the method
        final TypeAnnotatedExpression annotatedExpression = this.source.getTypeApproach1(context);

        // Get the method signature
        final String methodTypeName = annotatedExpression.typeName();
        final Optional<MethodTypeSignature> methodTypeSignature =
            context.typeTable.methodType(this.methodName, methodTypeName);
        if (methodTypeSignature.isEmpty()) throw new MethodNotFound(this.methodName, methodTypeName);

        // Make sure the correct number of arguments are passed
        final List<String> parametersTypes = methodTypeSignature.get().parameterTypeNames();
        if (this.args.size() != parametersTypes.size()) {
            throw new ArgsTypesMismatch(parametersTypes, this.args, context);
        }

        // <=> zip(this.args.map(lambdaMark), parametersTypes)
        var temp = new ArrayList<TypeMismatch>();
        for (int i = 0; i < this.args.size(); i++) {
            final FJExpr arg = this.args.get(i);
            final String type = parametersTypes.get(i);
            temp.add(new TypeMismatch(arg.lambdaMark(type), type));
        }

        // Check that arguments are correctly typed
        final var elaboratedArgs = new ArrayList<FJExpr>();
        for (final TypeMismatch tm : temp) {
            final TypeAnnotatedExpression ae = tm.expression().getTypeApproach1(context);
            elaboratedArgs.add(ae.expression());
            if (!context.typeTable.isSubtype(ae.typeName(), tm.expectedTypeName())) {
                throw new ArgTypeMismatch(tm.expectedTypeName(), ae.typeName());
            }
        }

        // Method invocation is correctly typed
        return new TypeAnnotatedExpression(
            methodTypeSignature.get().returnTypeName(),
            new FJMethodInvocation(annotatedExpression.expression(), this.methodName, elaboratedArgs)
        );
    }

    @Override
    public String getTypeNameApproach2(final TypeCheckingContext context) throws TypeError { // T-Invk
        final String typeName = this.source.getTypeNameApproach2(context);

        final Optional<MethodTypeSignature> methodTypeSignature =
            context.typeTable.methodType(this.methodName, typeName);
        if (methodTypeSignature.isEmpty()) throw new MethodNotFound(this.methodName, typeName);
        final List<String> parametersTypes = methodTypeSignature.get().parameterTypeNames();

        if (this.args.size() != parametersTypes.size()) {
            throw new ArgsTypesMismatch(parametersTypes, this.args, context);
        }

        var temp = new ArrayList<TypeMismatch>();
        for (int i = 0; i < this.args.size(); i++) {
            final FJExpr arg = this.args.get(i);
            final String type = parametersTypes.get(i);
            temp.add(new TypeMismatch(arg.lambdaMark(type), type));
        }

        // Check method invocation arguments typing
        for (final TypeMismatch tm : temp) {
            final String type = tm.expression().getTypeNameApproach2(context);
            if (!context.typeTable.isSubtype(type, tm.expectedTypeName())) {
                throw new ArgTypeMismatch(tm.expectedTypeName(), type);
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
    public Optional<FJExpr> _evalApproach2(final TypeTable typeTable) throws ClassNotFound {
        // If `this.source` has not been evaluated, evaluate it (recursivity).
        if (!this.source.isValue()) { // RC-Invk-Recv
            return this.source._evalApproach2(typeTable).map(e -> new FJMethodInvocation(e, this.methodName, this.args));
        }

        // If some arguments have not been evaluated, evaluate them and recursively evaluate the expression.
        if (!this.args.stream().allMatch(FJExpr::isValue)) { // RC-Invk-Arg
            //eq of: this.args.stream().map(e -> e._evalApproach2(typeTable)).flatMap(Optional::stream).toList();
            final List<FJExpr> args = new ArrayList<>();
            for (FJExpr arg : this.args) {
                arg._evalApproach2(typeTable).map(args::add);
            }
            return Optional.of(new FJMethodInvocation(this.source, this.methodName, args).evalApproach2(typeTable));
        }

        return this.source.evalMethodInvocationApproach2(typeTable, this);
    }

    @Override
    public Optional<FJExpr> substituteApproach2(List<String> parameterNames, List<FJExpr> args) {
        return this.source.substituteApproach2(parameterNames, args)
            .map(e -> {
                final List<FJExpr> _args = this.args.stream()
                    .map(a -> a.substituteApproach2(parameterNames, args))
                    .flatMap(Optional::stream).toList();
                return new FJMethodInvocation(e, this.methodName, _args);
            });
    }
}
