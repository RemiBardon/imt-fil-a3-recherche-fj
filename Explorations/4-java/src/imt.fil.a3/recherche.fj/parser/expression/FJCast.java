package imt.fil.a3.recherche.fj.parser.expression;

import imt.fil.a3.recherche.fj.FJUtils;
import imt.fil.a3.recherche.fj.parser.error.TypeError;
import imt.fil.a3.recherche.fj.parser.error.WrongCast;
import imt.fil.a3.recherche.fj.parser.type.FJType;

import java.util.HashMap;

public final class FJCast implements FJExpr {
    public final String typeName;
    public final FJExpr body;

    public FJCast(String typeName, FJExpr body) {
        this.typeName = typeName;
        this.body = body;
    }

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
    public Boolean isValue() {
        // NOTE: [Rémi BARDON] The original Haskell code says it should be
        //       only if `body instanceof FJLambda`, but I think it can be generalized.
        return body.isValue();
    }

    @Override
    public FJCast removingRuntimeAnnotation() {
        return new FJCast(this.typeName, this.body.removingRuntimeAnnotation());
    }
}
