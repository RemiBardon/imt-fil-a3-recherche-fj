package imt.fil.a3.recherche.fj.parser;

import imt.fil.a3.recherche.fj.parser.expression.FJExpr;

public final class TypeMismatch {
    public final FJExpr expression;
    public final String expectedTypeName;

    public TypeMismatch(FJExpr expression, String expectedTypeName) {
        this.expression = expression;
        this.expectedTypeName = expectedTypeName;
    }
}
