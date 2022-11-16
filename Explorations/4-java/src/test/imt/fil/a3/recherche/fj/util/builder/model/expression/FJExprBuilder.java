package imt.fil.a3.recherche.fj.util.builder.model.expression;

import imt.fil.a3.recherche.fj.model.java.expression.FJExpr;
import imt.fil.a3.recherche.fj.util.builder.FJBuilder;
import imt.fil.a3.recherche.fj.util.builder.error.FJBuilderException;

import java.util.function.Function;

public final class FJExprBuilder implements FJBuilder<FJExpr> {
    private IFJExprBuilder builder;

    @Override
    public FJExpr build() throws FJBuilderException {
        return this.builder.build();
    }

    FJExprBuilder cast(Function<FJCastBuilder, FJCastBuilder> update) {
        this.builder = update.apply(new FJCastBuilder());
        return this;
    }

    FJExprBuilder createObject(Function<FJCreateObjectBuilder, FJCreateObjectBuilder> update) {
        this.builder = update.apply(new FJCreateObjectBuilder());
        return this;
    }

    FJExprBuilder fieldAccess(Function<FJFieldAccessBuilder, FJFieldAccessBuilder> update) {
        this.builder = update.apply(new FJFieldAccessBuilder());
        return this;
    }

    FJExprBuilder lambda(Function<FJLambdaBuilder, FJLambdaBuilder> update) {
        this.builder = update.apply(new FJLambdaBuilder());
        return this;
    }

    FJExprBuilder methodInvocation(Function<FJMethodInvocationBuilder, FJMethodInvocationBuilder> update) {
        this.builder = update.apply(new FJMethodInvocationBuilder());
        return this;
    }

    FJExprBuilder variable(Function<FJVariableBuilder, FJVariableBuilder> update) {
        this.builder = update.apply(new FJVariableBuilder());
        return this;
    }


}
