package imt.fil.a3.recherche.fj.model.java.expression;

import imt.fil.a3.recherche.fj.model.TypeCheckingContext;
import imt.fil.a3.recherche.fj.model.TypeTable;
import imt.fil.a3.recherche.fj.model.error.TypeError;
import imt.fil.a3.recherche.fj.model.error.WrongLambdaType;
import imt.fil.a3.recherche.fj.model.java.misc.FJField;
import imt.fil.a3.recherche.fj.model.java.misc.FJSignature;
import imt.fil.a3.recherche.fj.model.misc.TypeAnnotatedExpression;

import java.util.List;
import java.util.Optional;

public record FJLambda(List<FJField> args, FJExpr body) implements FJExpr {
    @Override
    public TypeAnnotatedExpression getTypeApproach1(
        final TypeCheckingContext context
    ) throws TypeError {
        // Error: Lambda expression without a type
        throw new WrongLambdaType("None", this);
    }

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

    public TypeAnnotatedExpression getTypeApproach1(
        final TypeCheckingContext context,
        final String returnTypeName
    ) throws TypeError { // T-Lam
        final TypeCheckingContext lambdaContext = context.with(this.args);

        final Optional<List<FJSignature>> abstractMethods = context.typeTable.abstractMethods(returnTypeName);
        // Lambdas have only one abstract method: themselves
        if (abstractMethods.isEmpty() || abstractMethods.get().size() != 1) {
            throw new WrongLambdaType(returnTypeName, this);
        }
        final FJSignature method = abstractMethods.get().get(0);

        final TypeAnnotatedExpression annotatedLambda =
            this.lambdaMark(method.returnTypeName()).getTypeApproach1(lambdaContext);
        final String expectedTypeName = annotatedLambda.typeName();
        final boolean returnTypeIsCorrect = context.typeTable.isSubtype(expectedTypeName, method.returnTypeName());
        final boolean argsTypesAreCorrect = method.args().get(0).equals(this.args.get(0));

        if (returnTypeIsCorrect && argsTypesAreCorrect) {
            return new TypeAnnotatedExpression(
                returnTypeName,
                new FJCast(returnTypeName, new FJLambda(this.args, annotatedLambda.expression()))
            );
        } else {
            throw new WrongLambdaType(returnTypeName, this);
        }
    }

    public String getTypeNameApproach2(
        final TypeCheckingContext context,
        final String returnTypeName
    ) throws TypeError {
        final TypeCheckingContext lambdaContext = context.with(this.args);

        final Optional<List<FJSignature>> abstractMethods = context.typeTable.abstractMethods(returnTypeName);
        // Lambdas have only one abstract method: themselves
        if (abstractMethods.isEmpty() || abstractMethods.get().size() != 1) {
            throw new WrongLambdaType(returnTypeName, this);
        }
        final FJSignature method = abstractMethods.get().get(0);

        final String expectedTypeName = this.lambdaMark(method.returnTypeName()).getTypeNameApproach2(lambdaContext);
        final boolean returnTypeIsCorrect = context.typeTable.isSubtype(expectedTypeName, method.returnTypeName());
        final boolean argsTypesAreCorrect = method.args().get(0).equals(this.args.get(0));

        if (returnTypeIsCorrect && argsTypesAreCorrect) {
            return returnTypeName;
        } else {
            throw new WrongLambdaType(returnTypeName, this);
        }
    }
}
