package imt.fil.a3.recherche.fj.parser;

import imt.fil.a3.recherche.fj.parser.expression.FJExpr;

import java.util.List;

public final class FJMethodBodySignature {
    public final List<String> argumentNames;
    public final FJExpr body;

    public FJMethodBodySignature(List<String> argumentNames, FJExpr body) {
        this.argumentNames = argumentNames;
        this.body = body;
    }
}
