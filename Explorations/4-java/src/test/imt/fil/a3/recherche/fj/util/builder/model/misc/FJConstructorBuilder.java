package imt.fil.a3.recherche.fj.util.builder.model.misc;

import imt.fil.a3.recherche.fj.model.java.misc.FJConstructor;
import imt.fil.a3.recherche.fj.model.java.misc.FJField;
import imt.fil.a3.recherche.fj.model.misc.FieldInit;
import imt.fil.a3.recherche.fj.util.builder.FJBuilder;
import imt.fil.a3.recherche.fj.util.builder.misc.FieldInitBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class FJConstructorBuilder implements FJBuilder<FJConstructor> {
    private final List<FJFieldBuilder> args = new ArrayList<>();
    private final List<String> superArgs = new ArrayList<>();
    @SuppressWarnings("SpellCheckingInspection")
    private final List<FieldInitBuilder> fieldInits = new ArrayList<>();
    private String name;

    @Override
    public FJConstructor build() {
        final List<FJField> args = new ArrayList<>();
        for (final FJFieldBuilder arg : this.args) {
            args.add(arg.build());
        }
        // noinspection SpellCheckingInspection
        final List<FieldInit> fieldInits = new ArrayList<>();
        for (final FieldInitBuilder fi : this.fieldInits) {
            fieldInits.add(fi.build());
        }
        return new FJConstructor(this.name, args, this.superArgs, fieldInits);
    }

    public FJConstructorBuilder name(String name) {
        this.name = name;
        return this;
    }

    public FJConstructorBuilder superArg(String superArg) {
        this.superArgs.add(superArg);
        return this;
    }

    public FJConstructorBuilder arg(Function<FJFieldBuilder, FJFieldBuilder> arg) {
        this.args.add(arg.apply(new FJFieldBuilder()));
        return this;
    }

    public FJConstructorBuilder arg(final String typeName, final String paramName) {
        return this.arg(b -> b.type(typeName).name(paramName));
    }

    public FJConstructorBuilder fieldInit(Function<FieldInitBuilder, FieldInitBuilder> fieldInit) {
        this.fieldInits.add(fieldInit.apply(new FieldInitBuilder()));
        return this;
    }

    /**
     * Initializes an object field with a constructor argument of the same name (<=> `this.fieldName = fieldName`).
     */
    public FJConstructorBuilder fieldInit(final String fieldName) {
        return this.fieldInit(b -> b.fieldName(fieldName).argumentName(fieldName));
    }
}
