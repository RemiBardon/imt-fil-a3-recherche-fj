package imt.fil.a3.recherche.fj.util.builder.model.misc;

import imt.fil.a3.recherche.fj.model.java.misc.FJField;
import imt.fil.a3.recherche.fj.model.java.misc.FJSignature;
import imt.fil.a3.recherche.fj.util.builder.FJBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class FJSignatureBuilder implements FJBuilder<FJSignature> {
    private final List<FJFieldBuilder> args = new ArrayList<>();
    private String returnTypeName;
    private String name;

    @Override
    public FJSignature build() {
        final List<FJField> args = new ArrayList<>();
        for (final FJFieldBuilder arg : this.args) {
            args.add(arg.build());
        }
        return new FJSignature(this.returnTypeName, this.name, args);
    }

    public FJSignatureBuilder name(String name) {
        this.name = name;
        return this;
    }

    public FJSignatureBuilder returns(String returnTypeName) {
        this.returnTypeName = returnTypeName;
        return this;
    }

    public FJSignatureBuilder arg(final String typeName, final String paramName) {
        return this.arg(b -> b.type(typeName).name(paramName));
    }

    public FJSignatureBuilder arg(Function<FJFieldBuilder, FJFieldBuilder> update) {
        this.args.add(update.apply(new FJFieldBuilder()));
        return this;
    }
}

