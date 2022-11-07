package imt.fil.a3.recherche.fj.parser.error;

import imt.fil.a3.recherche.fj.parser.expression.FJExpr;

public final class WrongCast implements TypeError {
    public final String castType;
    public final FJExpr expression;

    public WrongCast(String castType, FJExpr expression) {
        this.castType = castType;
        this.expression = expression;
    }
}
