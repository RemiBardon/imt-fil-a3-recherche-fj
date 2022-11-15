package imt.fil.a3.recherche.fj.parser.expression;

import imt.fil.a3.recherche.fj.FJUtils;
import imt.fil.a3.recherche.fj.parser.error.TypeError;
import imt.fil.a3.recherche.fj.parser.error.WrongCast;
import imt.fil.a3.recherche.fj.parser.type.FJType;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public record FJCast(
    String typeName,
    FJExpr body
) implements FJExpr {
    @Override
    public String getTypeName(
        final HashMap<String, FJType> classTable,
        final HashMap<String, String> context
    ) throws TypeError {
        if (this.body instanceof final FJLambda lambda) { // T-Lam
            return lambda.getTypeName(classTable, context, this.typeName);
        } else {
            final String expectedTypeName = this.body.lambdaMark(this.typeName)
                .getTypeName(classTable, context);

            final boolean expectedTypeIsType =
                FJUtils.isSubtype(classTable, expectedTypeName, this.typeName);
            final boolean typeIsExpectedType =
                FJUtils.isSubtype(classTable, this.typeName, expectedTypeName);

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
    public Optional<FJExpr> _eval(final HashMap<String, FJType> classTable) {
        if (this.body().isValue()) {
            if (this.body() instanceof final FJCreateObject createObject) {
                if (FJUtils.isSubtype(classTable, createObject.className(), this.typeName())) { // R-Cast
                    return Optional.of(this.body());
                } else {
                    return Optional.empty();
                }
            } else if (this.body() instanceof final FJCast fjCast && fjCast.body() instanceof FJLambda) {
                if (FJUtils.isSubtype(classTable, fjCast.typeName(), this.typeName())) { // R-Cast-Lam
                    return Optional.of(this.body());
                } else {
                    return Optional.empty();
                }
            } else {
                return Optional.empty();
            }
        } else { // RC-Cast
            return this.body()._eval(classTable).map(e -> new FJCast(this.typeName(), e));
        }
    }

    @Override
    public Optional<FJExpr> substitute(final List<String> parameterNames, final List<FJExpr> args) {
        return this.body().substitute(parameterNames, args)
            .map(e -> new FJCast(this.typeName(), e));
    }
}
