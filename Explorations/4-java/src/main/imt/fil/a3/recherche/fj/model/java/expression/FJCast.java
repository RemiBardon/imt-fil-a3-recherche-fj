package imt.fil.a3.recherche.fj.model.java.expression;

import imt.fil.a3.recherche.fj.model.TypeCheckingContext;
import imt.fil.a3.recherche.fj.model.TypeTable;
import imt.fil.a3.recherche.fj.model.error.TypeError;
import imt.fil.a3.recherche.fj.model.error.WrongCast;
import imt.fil.a3.recherche.fj.model.java.misc.FJField;
import imt.fil.a3.recherche.fj.model.misc.MethodBodySignature;
import imt.fil.a3.recherche.fj.model.misc.MethodTypeSignature;
import imt.fil.a3.recherche.fj.model.misc.TypeAnnotatedExpression;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record FJCast(
    String typeName,
    FJExpr body
) implements FJExpr {
    @Override
    public TypeAnnotatedExpression getTypeApproach1(final TypeCheckingContext context) throws TypeError {
        if (this.body instanceof final FJLambda lambda) { // T-Lam
            return lambda.getTypeApproach1(context, this.typeName);
        } else {
            final TypeAnnotatedExpression annotatedBody = this.body.lambdaMark(this.typeName).getTypeApproach1(context);
            final String expectedTypeName = annotatedBody.typeName();

            final boolean expectedTypeIsType = context.typeTable.isSubtype(expectedTypeName, this.typeName);
            final boolean typeIsExpectedType = context.typeTable.isSubtype(this.typeName, expectedTypeName);

            if ((expectedTypeIsType) // T-UCast
                || (typeIsExpectedType && !this.typeName.equals(expectedTypeName)) // T-DCast
                || (!typeIsExpectedType) /* && !expectedTypeIsType */ // T-SCast
            ) {
                return new TypeAnnotatedExpression(this.typeName, annotatedBody.expression());
            } else {
                throw new WrongCast(this.typeName, annotatedBody.expression());
            }
        }
    }

    @Override
    public String getTypeNameApproach2(final TypeCheckingContext context) throws TypeError {
        if (this.body instanceof final FJLambda lambda) { // T-Lam
            return lambda.getTypeNameApproach2(context, this.typeName);
        } else {
            final String expectedTypeName = this.body.lambdaMark(this.typeName).getTypeNameApproach2(context);

            final boolean expectedTypeIsType = context.typeTable.isSubtype(expectedTypeName, this.typeName);
            final boolean typeIsExpectedType = context.typeTable.isSubtype(this.typeName, expectedTypeName);

            if ((expectedTypeIsType) // T-UCast
                || (typeIsExpectedType && !this.typeName.equals(expectedTypeName)) // T-DCast
                || (!typeIsExpectedType) /* && !expectedTypeIsType */ // T-SCast
            ) {
                return this.typeName;
            } else {
                throw new WrongCast(this.typeName, this.body);
            }
        }
    }

    @Override
    public FJCast removingRuntimeAnnotation() {
        return new FJCast(this.typeName, this.body.removingRuntimeAnnotation());
    }

    @Override
    public Boolean isValue() {
        // NOTE: [Rémi BARDON] The original Haskell code says it should be
        //       only if `body instanceof FJLambda`, but I think it can be generalized.
        return body.isValue();
    }

    @Override
    public Optional<FJExpr> _evalApproach2(final TypeTable typeTable) {
        if (this.body.isValue()) {
            if (this.body instanceof final FJCreateObject createObject) {
                if (typeTable.isSubtype(createObject.className(), this.typeName)) { // R-Cast
                    return Optional.of(this.body);
                } else {
                    return Optional.empty();
                }
            } else if (this.body instanceof final FJCast fjCast && fjCast.body() instanceof FJLambda) {
                if (typeTable.isSubtype(fjCast.typeName(), this.typeName)) { // R-Cast-Lam
                    return Optional.of(this.body);
                } else {
                    return Optional.empty();
                }
            } else {
                return Optional.empty();
            }
        } else { // RC-Cast
            return this.body._evalApproach2(typeTable).map(e -> new FJCast(this.typeName, e));
        }
    }

    @Override
    public Optional<FJExpr> substituteApproach2(final List<String> parameterNames, final List<FJExpr> args) {
        return this.body.substituteApproach2(parameterNames, args).map(e -> new FJCast(this.typeName, e));
    }

    @Override
    public Optional<FJExpr> evalMethodInvocationApproach2(
        final TypeTable typeTable,
        final FJMethodInvocation invocation
    ) {
        if (this.body instanceof final FJLambda lambda) {
            final Optional<MethodTypeSignature> methodType =
                typeTable.methodType(invocation.methodName(), this.typeName);
            if (methodType.isEmpty()) return Optional.empty(); // No method type

            final List<FJExpr> args2 = new ArrayList<>();
            // <=> zip(invocation.args(), methodType.parameterTypeNames)
            for (int i = 0; i < invocation.args().size(); i++) {
                final FJExpr arg = invocation.args().get(i);
                final String typeName = methodType.get().parameterTypeNames().get(i);
                args2.add(arg.lambdaMark(typeName));
            }

            final Optional<MethodBodySignature> methodBody =
                typeTable.methodBody(invocation.methodName(), this.typeName);
            if (methodBody.isPresent()) { // R-Default
                return methodBody.get().body()
                    .lambdaMark(methodType.get().returnTypeName())
                    .substituteApproach2(methodBody.get().argumentNames(), args2);
            } else { // R-Lam
                return lambda.body()
                    .lambdaMark(methodType.get().returnTypeName())
                    .substituteApproach2(lambda.args().stream().map(FJField::name).toList(), args2);
            }
        } else {
            return Optional.empty();
        }
    }
}
