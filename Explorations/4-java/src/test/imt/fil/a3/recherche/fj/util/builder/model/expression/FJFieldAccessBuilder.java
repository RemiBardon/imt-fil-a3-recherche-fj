package imt.fil.a3.recherche.fj.util.builder.model.expression;

import imt.fil.a3.recherche.fj.model.java.expression.FJFieldAccess;
import imt.fil.a3.recherche.fj.util.builder.error.FJBuilderException;

import java.util.function.Function;

public final class FJFieldAccessBuilder implements IFJExprBuilder {
    private FJExprBuilder object;
    private String fieldName;

    @Override
    public FJFieldAccess build() throws FJBuilderException {
        return new FJFieldAccess(this.object.build(), this.fieldName);
    }

    public FJFieldAccessBuilder object(Function<FJExprBuilder, FJExprBuilder> update) {
        this.object = update.apply(new FJExprBuilder());
        return this;
    }

    public FJFieldAccessBuilder fieldName(String fieldName) {
        this.fieldName = fieldName;
        return this;
    }
}
