package imt.fil.a3.recherche.fj.util.builder.misc;

import imt.fil.a3.recherche.fj.model.misc.FieldInit;
import imt.fil.a3.recherche.fj.util.builder.FJBuilder;
import imt.fil.a3.recherche.fj.util.builder.error.FJBuilderException;

public class FieldInitBuilder implements FJBuilder<FieldInit> {
    private String fieldName;
    private String argumentName;

    @Override
    public FieldInit build() throws FJBuilderException {
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
