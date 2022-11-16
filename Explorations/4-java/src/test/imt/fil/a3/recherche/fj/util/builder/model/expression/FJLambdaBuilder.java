package imt.fil.a3.recherche.fj.util.builder.model.expression;

import imt.fil.a3.recherche.fj.model.java.expression.FJLambda;
import imt.fil.a3.recherche.fj.model.java.misc.FJField;
import imt.fil.a3.recherche.fj.util.builder.error.FJBuilderException;
import imt.fil.a3.recherche.fj.util.builder.model.misc.FJFieldBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class FJLambdaBuilder implements IFJExprBuilder {
    private final List<FJFieldBuilder> args = new ArrayList<>();
    private FJExprBuilder body;

    @Override
    public FJLambda build() throws FJBuilderException {
        final List<FJField> args = new ArrayList<>();
        for (final FJFieldBuilder arg : this.args) {
            args.add(arg.build());
        }
        return new FJLambda(args, this.body.build());
    }

    public FJLambdaBuilder arg(Function<FJFieldBuilder, FJFieldBuilder> update) {
        this.args.add(update.apply(new FJFieldBuilder()));
        return this;
    }

    public FJLambdaBuilder body(Function<FJExprBuilder, FJExprBuilder> update) {
        this.body = update.apply(new FJExprBuilder());
        return this;
    }
}
