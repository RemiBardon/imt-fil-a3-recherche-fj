package imt.fil.a3.recherche.fj.util.builder.model.expression;

import imt.fil.a3.recherche.fj.model.java.expression.FJFieldAccess;
import imt.fil.a3.recherche.fj.util.builder.FJBuilder;
import imt.fil.a3.recherche.fj.util.builder.error.FJBuilderException;

import java.util.function.Function;

public final class FJFieldAccessBuilder implements FJBuilder<FJFieldAccess> {
    private FJExprBuilder object;
    private String fieldName;

    @Override
    public FJFieldAccess build() throws FJBuilderException {
        throw new RuntimeException();
    }

    FJFieldAccessBuilder object(Function<FJExprBuilder, FJExprBuilder> update) {
        this.object = update.apply(new FJExprBuilder());
        return this;
    }

    FJFieldAccessBuilder fieldName(String fieldName) {
        this.fieldName = fieldName;
        return this;
    }
}
