package imt.fil.a3.recherche.fj.util.builder.model.expression;

import imt.fil.a3.recherche.fj.model.java.expression.FJVariable;
import imt.fil.a3.recherche.fj.util.builder.error.FJBuilderException;

public final class FJVariableBuilder implements IFJExprBuilder {
    private String name;

    @Override
    public FJVariable build() throws FJBuilderException {
        return new FJVariable(this.name);
    }

    public FJVariableBuilder name(String name) {
        this.name = name;
        return this;
    }
}
