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
        // we apply lambdamark on the λ-expression body e with the return type T of the abstract method, once it can be another λ-expression, producing a new term e2
        final FJExpr body = this.body.lambdaMark(method.returnTypeName());
        // we apply the typing judgment T on the new body e2 with the formal parameters of the λ-expression added to context Γ, resulting in a new body e′′
        final TypeAnnotatedExpression typedBody = body.getTypeApproach1(lambdaContext);

        // we check that the type of e′′ is a subtype of the return type T of the abstract method
        final String expectedTypeName = typedBody.typeName();
        final boolean returnTypeIsCorrect = context.typeTable.isSubtype(expectedTypeName, method.returnTypeName());
        final boolean argsTypesAreCorrect = method.args().get(0).type().equals(this.args.get(0).type());

        // we check that the type of e′′ is a subtype of the return type T of the abstract method
        if (returnTypeIsCorrect && argsTypesAreCorrect) {
            // we return the annotated cast of the λ-expression with the return type T of the abstract method and the body e′′
            String lambdaTypeName = String.format("(%s)->%s", this.args.get(0).type(), method.returnTypeName());
            return new TypeAnnotatedExpression(
                    lambdaTypeName,
                    new FJCast(returnTypeName, this));
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

        //, applies λmark in the body expression e with the return type T of the method m
        final FJExpr body = this.body.lambdaMark(method.returnTypeName());
        //it verifies if the resulting type of the body is a subtype of the return type of the method m.
        final String bodyTypeName = body.getTypeNameApproach2(lambdaContext);

        final boolean returnTypeIsCorrect = context.typeTable.isSubtype(bodyTypeName, method.returnTypeName());
        final boolean argsTypesAreCorrect = method.args().get(0).type().equals(this.args.get(0).type());
        String lambdaTypeName = String.format("(%s)->%s", this.args.get(0).type(), method.returnTypeName());
        if (returnTypeIsCorrect && argsTypesAreCorrect) {
            return lambdaTypeName;
        } else {
            throw new WrongLambdaType(lambdaTypeName, this);
        }
    }
}
