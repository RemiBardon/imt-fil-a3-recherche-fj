package imt.fil.a3.recherche.fj.util.builder.model.expression;

import imt.fil.a3.recherche.fj.model.java.expression.FJCreateObject;
import imt.fil.a3.recherche.fj.model.java.expression.FJExpr;
import imt.fil.a3.recherche.fj.util.builder.error.FJBuilderException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class FJCreateObjectBuilder implements IFJExprBuilder {
    private final List<FJExprBuilder> args = new ArrayList<>();
    private String className;

    @Override
    public FJCreateObject build() throws FJBuilderException {
        final List<FJExpr> args = new ArrayList<>();
        for (final FJExprBuilder arg : this.args) {
            args.add(arg.build());
        }
        return new FJCreateObject(this.className, args);
    }

    public FJCreateObjectBuilder className(String className) {
        this.className = className;
        return this;
    }

    public FJCreateObjectBuilder arg(Function<FJExprBuilder, FJExprBuilder> arg) {
        this.args.add(arg.apply(new FJExprBuilder()));
        return this;
    }
}
