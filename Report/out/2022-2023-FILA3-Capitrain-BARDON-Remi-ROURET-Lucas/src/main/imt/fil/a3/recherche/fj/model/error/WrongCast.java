package imt.fil.a3.recherche.fj.model.error;

import imt.fil.a3.recherche.fj.model.java.expression.FJExpr;

public final class WrongCast extends TypeError {
    public final String castType;
    public final FJExpr expression;

    public WrongCast(String castType, FJExpr expression) {
        this.castType = castType;
        this.expression = expression;
    }

    @Override
    public String getMessage() {
        return "Expression cannot be casted to `" + this.castType + "`.";
    }
}
