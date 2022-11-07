package imt.fil.a3.recherche.fj.parser.expression;

import java.util.List;

public final class FJCreateObject implements FJExpr {
    public final String typeName;
    public final List<FJExpr> args;

    public FJCreateObject(String typeName, List<FJExpr> args) {
        this.typeName = typeName;
        this.args = args;
    }
}
