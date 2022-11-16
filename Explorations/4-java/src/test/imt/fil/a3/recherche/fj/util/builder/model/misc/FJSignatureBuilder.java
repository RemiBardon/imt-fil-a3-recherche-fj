package imt.fil.a3.recherche.fj.util.builder.model.misc;

import imt.fil.a3.recherche.fj.model.java.misc.FJSignature;
import imt.fil.a3.recherche.fj.util.builder.FJBuilder;
import imt.fil.a3.recherche.fj.util.builder.error.FJBuilderException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class FJSignatureBuilder implements FJBuilder<FJSignature> {
    private String returnTypeName;
    private String name;
    private final List<FJFieldBuilder> args = new ArrayList<>();

    @Override
    public FJSignature build() throws FJBuilderException {
        throw new RuntimeException();
    }

    public FJSignatureBuilder name(String name) {
        this.name = name;
        return this;
    }

    public FJSignatureBuilder returnTypeName(String returnTypeName) {
        this.returnTypeName = returnTypeName;
        return this;
    }

    public FJSignatureBuilder arg(Function<FJFieldBuilder, FJFieldBuilder> update) {
        this.args.add(update.apply(new FJFieldBuilder()));
        return this;
    }
}

