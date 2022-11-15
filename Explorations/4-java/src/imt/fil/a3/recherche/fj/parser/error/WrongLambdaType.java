package imt.fil.a3.recherche.fj.parser.error;

import imt.fil.a3.recherche.fj.parser.expression.FJExpr;

public final class WrongLambdaType extends TypeError {
    public final String targetTypeName;
    public final FJExpr lambda;

    public WrongLambdaType(String targetTypeName, FJExpr lambda) {
        this.targetTypeName = targetTypeName;
        this.lambda = lambda;
    }
}
