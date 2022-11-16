package imt.fil.a3.recherche.fj.util.builder.model.expression;

import imt.fil.a3.recherche.fj.model.java.expression.FJCreateObject;
import imt.fil.a3.recherche.fj.util.builder.FJBuilder;
import imt.fil.a3.recherche.fj.util.builder.error.FJBuilderException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class FJCreateObjectBuilder implements FJBuilder<FJCreateObject> {
    private String className;
    private final List<FJExprBuilder> args = new ArrayList<>();

    @Override
    public FJCreateObject build() throws FJBuilderException {
        throw new RuntimeException();
    }

    public FJCreateObjectBuilder className(String className) {
        this.className = className;
        return this;
    }

    public FJCreateObjectBuilder arg(Function<FJExprBuilder,FJExprBuilder> arg) {
        this.args.add(arg.apply(new FJExprBuilder()));
        return this;
    }
}
