package imt.fil.a3.recherche.fj.model.error;

import imt.fil.a3.recherche.fj.model.TypeCheckingContext;
import imt.fil.a3.recherche.fj.model.java.expression.FJExpr;

import java.util.List;

public final class ArgsTypesMismatch extends TypeError {
    public final List<String> expected;
    public final List<String> actual;

    public ArgsTypesMismatch(List<String> expectedTypes, List<FJExpr> args, TypeCheckingContext context) {
        this(
            expectedTypes,
            args.stream().map(a -> {
                try {
                    return a.getTypeName(context);
                } catch (TypeError e) {
                    e.printStackTrace();
                    return "<null>";
                }
            }).toList()
        );
    }

    public ArgsTypesMismatch(List<String> expected, List<String> actual) {
        this.expected = expected;
        this.actual = actual;
    }

    @Override
    public String getMessage() {
        return "Arguments not typed correctly: expected `(" + String.join(", ", this.expected)
            + ")` got `(" + String.join(", ", this.actual) + ")`.";
    }
}
