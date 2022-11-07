package imt.fil.a3.recherche.fj.parser.expression;

import imt.fil.a3.recherche.fj.parser.FJField;

import java.util.List;

public final class FJLambda implements FJExpr {
    public final List<FJField> args;
    public final FJExpr body;

    public FJLambda(List<FJField> args, FJExpr body) {
        this.args = args;
        this.body = body;
    }
}
