package imt.fil.a3.recherche.fj.parser;

import imt.fil.a3.recherche.fj.parser.expression.FJExpr;

public final class FJMethod {
    public final FJSignature signature;
    public final FJExpr body;

    public FJMethod(FJSignature signature, FJExpr body) {
        this.signature = signature;
        this.body = body;
    }
}
