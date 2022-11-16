package imt.fil.a3.recherche.fj.model.error;

import imt.fil.a3.recherche.fj.model.java.expression.FJExpr;

public final class WrongLambdaType extends TypeError {
    public final String targetTypeName;
    public final FJExpr lambda;

    public WrongLambdaType(String targetTypeName, FJExpr lambda) {
        this.targetTypeName = targetTypeName;
        this.lambda = lambda;
    }

    @Override
    public String getMessage() {
        return "Incorrect lambda return type (expected `" + this.targetTypeName + "`).";
    }
}
