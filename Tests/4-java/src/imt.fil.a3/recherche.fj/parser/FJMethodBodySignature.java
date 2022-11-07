package imt.fil.a3.recherche.fj.parser;

import imt.fil.a3.recherche.fj.parser.expression.FJExpr;

import java.util.List;

public final class FJMethodBodySignature {
    public final List<String> argumentNames;
    public final FJExpr bodys;

    public FJMethodBodySignature(List<String> argumentNames, FJExpr bodys) {
        this.argumentNames = argumentNames;
        this.bodys = bodys;
    }
}
