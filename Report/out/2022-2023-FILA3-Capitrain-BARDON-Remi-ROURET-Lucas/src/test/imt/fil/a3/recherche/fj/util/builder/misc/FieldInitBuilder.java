package imt.fil.a3.recherche.fj.util.builder.misc;

import imt.fil.a3.recherche.fj.model.misc.FieldInit;
import imt.fil.a3.recherche.fj.util.builder.FJBuilder;

public class FieldInitBuilder implements FJBuilder<FieldInit> {
    private String fieldName;
    private String argumentName;

    @Override
    public FieldInit build() {
        return new FieldInit(this.fieldName, this.argumentName);
    }

    public FieldInitBuilder fieldName(String fieldName) {
        this.fieldName = fieldName;
        return this;
    }

    public FieldInitBuilder argumentName(String argumentName) {
        this.argumentName = argumentName;
        return this;
    }
}
