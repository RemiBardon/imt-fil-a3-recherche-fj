package imt.fil.a3.recherche.fj.model.java.expression;

import imt.fil.a3.recherche.fj.model.TypeCheckingContext;
import imt.fil.a3.recherche.fj.model.TypeTable;
import imt.fil.a3.recherche.fj.model.error.ArgTypeMismatch;
import imt.fil.a3.recherche.fj.model.error.ArgsTypesMismatch;
import imt.fil.a3.recherche.fj.model.error.MethodNotFound;
import imt.fil.a3.recherche.fj.model.error.TypeError;
import imt.fil.a3.recherche.fj.model.misc.MethodTypeSignature;
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
    public String getTypeNameApproach2(final TypeCheckingContext context) throws TypeError { // T-Invk
        final String typeName = this.source.getTypeNameApproach2(context);

        final Optional<MethodTypeSignature> methodTypeSignature =
            context.typeTable.methodType(this.methodName, typeName);
        if (methodTypeSignature.isEmpty()) throw new MethodNotFound(this.methodName, typeName);
        final List<String> parameterTypes = methodTypeSignature.get().parameterTypeNames();

        if (this.args.size() != parameterTypes.size()) {
            throw new ArgsTypesMismatch(parameterTypes, this.args, context);
        }

        var temp = new ArrayList<TypeMismatch>();
        for (int i = 0; i < this.args.size(); i++) {
            final FJExpr arg = this.args.get(i);
            final String type = parameterTypes.get(i);
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
    public Optional<FJExpr> _evalApproach2(final TypeTable typeTable) {
        // If `this.source` has not been evaluated, evaluate it (recursivity).
        if (!this.source.isValue()) { // RC-Invk-Recv
            return this.source._evalApproach2(typeTable).map(e -> new FJMethodInvocation(e, this.methodName, this.args));
        }

        // If some arguments have not been evaluated, evaluate them and recursively evaluate the expression.
        if (!this.args.stream().allMatch(FJExpr::isValue)) { // RC-Invk-Arg
            final List<FJExpr> args = this.args.stream().map(e -> e._evalApproach2(typeTable))
                .flatMap(Optional::stream).toList();
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
