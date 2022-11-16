package imt.fil.a3.recherche.fj.util.builder.model.type;

import imt.fil.a3.recherche.fj.model.java.misc.FJField;
import imt.fil.a3.recherche.fj.model.java.misc.FJMethod;
import imt.fil.a3.recherche.fj.model.java.type.FJClass;
import imt.fil.a3.recherche.fj.util.builder.FJBuilder;
import imt.fil.a3.recherche.fj.util.builder.error.FJBuilderException;
import imt.fil.a3.recherche.fj.util.builder.model.misc.FJConstructorBuilder;
import imt.fil.a3.recherche.fj.util.builder.model.misc.FJFieldBuilder;
import imt.fil.a3.recherche.fj.util.builder.model.misc.FJMethodBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class FJClassBuilder implements FJBuilder<FJClass> {
    private final List<String> implementsNames = new ArrayList<>();
    private final List<FJFieldBuilder> fields = new ArrayList<>();
    private final List<FJMethodBuilder> methods = new ArrayList<>();
    private String name;
    private String extendsName = "Object";
    private FJConstructorBuilder constructor;

    @Override
    public FJClass build() throws FJBuilderException {
        final List<FJField> fields = new ArrayList<>();
        for (final FJFieldBuilder arg : this.fields) {
            fields.add(arg.build());
        }
        final List<FJMethod> methods = new ArrayList<>();
        for (final FJMethodBuilder method : this.methods) {
            methods.add(method.build());
        }
        return new FJClass(
            this.name,
            this.extendsName,
            this.implementsNames,
            fields,
            methods,
            this.constructor.build()
        );
    }

    public FJClassBuilder name(String name) {
        this.name = name;
        return this;
    }

    public FJClassBuilder extendsName(String extendsName) {
        this.extendsName = extendsName;
        return this;
    }

    public FJClassBuilder implement(String interfaceName) {
        implementsNames.add(interfaceName);
        return this;
    }

    public FJClassBuilder field(Function<FJFieldBuilder, FJFieldBuilder> update) {
        fields.add(update.apply(new FJFieldBuilder()));
        return this;
    }

    public FJClassBuilder method(Function<FJMethodBuilder, FJMethodBuilder> update) {
        this.methods.add(update.apply(new FJMethodBuilder()));
        return this;
    }

    public FJClassBuilder constructor(Function<FJConstructorBuilder, FJConstructorBuilder> update) {
        this.constructor = new FJConstructorBuilder();
        update.apply(this.constructor);
        return this;
    }
}
