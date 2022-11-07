package imt.fil.a3.recherche.fj.parser.expression;

public final class FJCast implements FJExpr {
    public final String typeName;
    public final FJExpr body;

    public FJCast(String typeName, FJExpr body) {
        this.typeName = typeName;
        this.body = body;
    }
}
