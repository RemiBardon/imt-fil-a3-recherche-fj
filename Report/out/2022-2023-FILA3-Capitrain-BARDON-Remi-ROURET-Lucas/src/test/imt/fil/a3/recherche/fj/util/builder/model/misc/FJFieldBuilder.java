package imt.fil.a3.recherche.fj.util.builder.model.misc;

import imt.fil.a3.recherche.fj.model.java.misc.FJField;
import imt.fil.a3.recherche.fj.util.builder.FJBuilder;

public final class FJFieldBuilder implements FJBuilder<FJField> {
    private String type;
    private String name;

    @Override
    public FJField build() {
        return new FJField(this.type, this.name);
    }

    public FJFieldBuilder name(String name) {
        this.name = name;
        return this;
    }

    public FJFieldBuilder type(String type) {
        this.type = type;
        return this;
    }
}
