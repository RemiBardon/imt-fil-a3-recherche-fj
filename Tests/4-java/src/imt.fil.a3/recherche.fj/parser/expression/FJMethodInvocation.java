package imt.fil.a3.recherche.fj.parser.expression;

import java.util.List;

public final class FJMethodInvocation implements FJExpr {
    public final FJExpr object;
    public final String methodName;
    public final List<FJExpr> arguments;

    public FJMethodInvocation(FJExpr object, String methodName, List<FJExpr> arguments) {
        this.object = object;
        this.methodName = methodName;
        this.arguments = arguments;
    }
}
