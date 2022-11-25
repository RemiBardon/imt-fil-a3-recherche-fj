package imt.fil.a3.recherche.fj.util.builder.model.expression;

import imt.fil.a3.recherche.fj.model.java.expression.FJExpr;
import imt.fil.a3.recherche.fj.model.java.expression.FJMethodInvocation;
import imt.fil.a3.recherche.fj.util.builder.error.FJBuilderException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class FJMethodInvocationBuilder implements IFJExprBuilder {
    private final List<FJExprBuilder> args = new ArrayList<>();
    private FJExprBuilder source;
    private String methodName;

    @Override
    public FJMethodInvocation build() throws FJBuilderException {
        final List<FJExpr> args = new ArrayList<>();
        for (final FJExprBuilder arg : this.args) {
            args.add(arg.build());
        }
        return new FJMethodInvocation(this.source.build(), this.methodName, args);
    }

    public FJMethodInvocationBuilder source(Function<FJExprBuilder, FJExprBuilder> source) {
        this.source = source.apply(new FJExprBuilder());
        return this;
    }

    public FJMethodInvocationBuilder methodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public FJMethodInvocationBuilder arg(Function<FJExprBuilder, FJExprBuilder> arg) {
        this.args.add(arg.apply(new FJExprBuilder()));
        return this;
    }
}
