package imt.fil.a3.recherche.fj.model.java.expression;

import imt.fil.a3.recherche.fj.model.TypeCheckingContext;
import imt.fil.a3.recherche.fj.model.TypeTable;
import imt.fil.a3.recherche.fj.model.error.TypeError;
import imt.fil.a3.recherche.fj.model.error.WrongLambdaType;
import imt.fil.a3.recherche.fj.model.java.misc.FJField;
import imt.fil.a3.recherche.fj.model.java.misc.FJSignature;

import java.util.List;
import java.util.Optional;

public record FJLambda(List<FJField> args, FJExpr body) implements FJExpr {
    @Override
    public String getTypeNameApproach2(
        final TypeCheckingContext context
    ) throws TypeError {
        // Error: Lambda expression without a type
        throw new WrongLambdaType("None", this);
    }

    @Override
    public FJExpr lambdaMark(final String typeName) {
        return new FJCast(typeName, this);
    }

    @Override
    public FJLambda removingRuntimeAnnotation() {
        return new FJLambda(this.args, this.body.removingRuntimeAnnotation());
    }

    @Override
    public Boolean isValue() { return true; }

    @Override
    public Optional<FJExpr> _evalApproach2(TypeTable typeTable) { return Optional.of(this); }

    @Override
    public Optional<FJExpr> substituteApproach2(List<String> parameterNames, List<FJExpr> args) {
        return Optional.of(this); // Do nothing
    }

    public String getTypeName(final TypeCheckingContext context, final String returnType) throws TypeError {
        final TypeCheckingContext lambdaContext = context.with(this.args);

        final Optional<List<FJSignature>> abstractMethods = context.typeTable.abstractMethods(returnType);
        // Lambdas have only one abstract method: themselves
        if (abstractMethods.isEmpty() || abstractMethods.get().size() != 1) {
            throw new WrongLambdaType(returnType, this);
        }
        final FJSignature method = abstractMethods.get().get(0);

        final String expectedTypeName = this.lambdaMark(method.returnTypeName()).getTypeNameApproach2(lambdaContext);
        final boolean returnTypeIsCorrect = context.typeTable.isSubtype(expectedTypeName, method.returnTypeName());
        final boolean argsTypesAreCorrect = method.args().get(0).equals(this.args.get(0));

        if (returnTypeIsCorrect && argsTypesAreCorrect) {
            return returnType;
        } else {
            throw new WrongLambdaType(returnType, this);
        }
    }
}
