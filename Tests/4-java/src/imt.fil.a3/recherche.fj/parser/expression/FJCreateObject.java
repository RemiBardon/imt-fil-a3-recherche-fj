package imt.fil.a3.recherche.fj.parser.expression;

import java.util.List;

public final class FJCreateObject implements FJExpr {
    public final String className;
    public final List<FJExpr> args;

    public FJCreateObject(String className, List<FJExpr> args) {
        this.className = className;
        this.args = args;
    }
}
