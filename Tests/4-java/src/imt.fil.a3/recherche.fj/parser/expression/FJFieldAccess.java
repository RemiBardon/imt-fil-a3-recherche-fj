package imt.fil.a3.recherche.fj.parser.expression;

public final class FJFieldAccess implements FJExpr {
    public final FJExpr object;
    public final String fieldName;

    public FJFieldAccess(FJExpr object, String fieldName) {
        this.object = object;
        this.fieldName = fieldName;
    }
}
