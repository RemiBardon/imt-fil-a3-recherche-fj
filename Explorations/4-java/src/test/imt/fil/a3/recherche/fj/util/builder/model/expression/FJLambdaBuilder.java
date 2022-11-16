package imt.fil.a3.recherche.fj.util.builder.model.expression;

import imt.fil.a3.recherche.fj.model.java.expression.FJLambda;
import imt.fil.a3.recherche.fj.util.builder.FJBuilder;
import imt.fil.a3.recherche.fj.util.builder.error.FJBuilderException;
import imt.fil.a3.recherche.fj.util.builder.model.misc.FJFieldBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class FJLambdaBuilder implements FJBuilder<FJLambda> {
    private final List<FJFieldBuilder> args = new ArrayList<>();
    private FJExprBuilder body;

    @Override
    public FJLambda build() throws FJBuilderException {
        throw new RuntimeException();
    }

    FJLambdaBuilder arg(Function<FJFieldBuilder, FJFieldBuilder> update) {
        this.args.add(update.apply(new FJFieldBuilder()));
        return this;
    }

    FJLambdaBuilder body(Function<FJExprBuilder, FJExprBuilder> update) {
        this.body = update.apply(new FJExprBuilder());
        return this;
    }
}
