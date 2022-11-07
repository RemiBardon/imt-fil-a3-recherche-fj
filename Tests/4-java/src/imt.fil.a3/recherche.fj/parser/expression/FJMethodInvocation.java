package imt.fil.a3.recherche.fj.parser.expression;

import java.util.List;

public final class FJMethodInvocation implements FJExpr {
    public final FJExpr source;
    public final String methodName;
    public final List<FJExpr> args;

    public FJMethodInvocation(FJExpr source, String methodName, List<FJExpr> args) {
        this.source = source;
        this.methodName = methodName;
        this.args = args;
    }
}
