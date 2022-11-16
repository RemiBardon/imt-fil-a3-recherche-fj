package imt.fil.a3.recherche.fj.util.builder.model.expression;

import imt.fil.a3.recherche.fj.model.java.expression.FJCast;
import imt.fil.a3.recherche.fj.util.builder.FJBuilder;
import imt.fil.a3.recherche.fj.util.builder.error.FJBuilderException;

import java.util.function.Function;


final public class FJCastBuilder implements IFJExprBuilder {
    private String typeName;
    private FJExprBuilder body;

    @Override
    public FJCast build() throws FJBuilderException {
        return new FJCast(this.typeName, this.body.build());
    }

    public FJCastBuilder typeName(String typeName){
        this.typeName = typeName;
        return this;
    }

    public FJCastBuilder body(Function<FJExprBuilder,FJExprBuilder> body){
        this.body = body.apply(new FJExprBuilder());
        return this;
    }
}
