package imt.fil.a3.recherche.fj.util.builder.model.misc;

import imt.fil.a3.recherche.fj.model.java.misc.FJMethod;
import imt.fil.a3.recherche.fj.util.builder.FJBuilder;
import imt.fil.a3.recherche.fj.util.builder.error.FJBuilderException;
import imt.fil.a3.recherche.fj.util.builder.model.expression.FJExprBuilder;

import java.util.function.Function;

public final class FJMethodBuilder implements FJBuilder<FJMethod> {
    private FJSignatureBuilder signature;
    private FJExprBuilder body;

    @Override
    public FJMethod build() throws FJBuilderException {
        return new FJMethod(this.signature.build(), this.body.build());
    }

    public FJMethodBuilder signature(Function<FJSignatureBuilder, FJSignatureBuilder> update) {
        this.signature = update.apply(new FJSignatureBuilder());
        return this;
    }

    public FJMethodBuilder body(Function<FJExprBuilder, FJExprBuilder> update) {
        this.body = update.apply(new FJExprBuilder());
        return this;
    }
}
