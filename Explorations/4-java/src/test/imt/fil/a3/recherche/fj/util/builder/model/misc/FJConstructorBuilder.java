package imt.fil.a3.recherche.fj.util.builder.model.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import imt.fil.a3.recherche.fj.model.java.misc.FJConstructor;
import imt.fil.a3.recherche.fj.util.builder.FJBuilder;
import imt.fil.a3.recherche.fj.util.builder.error.FJBuilderException;
import imt.fil.a3.recherche.fj.util.builder.misc.FieldInitBuilder;

public final class FJConstructorBuilder implements FJBuilder<FJConstructor> {

    private String name;
    private final List<FJFieldBuilder> args = new ArrayList<>();
    private final List<String> superArgs = new ArrayList<>();
    @SuppressWarnings("SpellCheckingInspection")
    private final List<FieldInitBuilder> fieldInits = new ArrayList<>();

    @Override
    public FJConstructor build() throws FJBuilderException {
        throw new RuntimeException();
    }

    public FJConstructorBuilder name(String name) {
        this.name = name;
        return this;
    }

    public FJConstructorBuilder superArg(String superArg){
        this.superArgs.add(superArg);
        return this;
    }

    public FJConstructorBuilder arg(Function<FJFieldBuilder,FJFieldBuilder> arg){
        this.args.add(arg.apply(new FJFieldBuilder()));
        return this;
    }

    public FJConstructorBuilder fieldInit(Function<FieldInitBuilder,FieldInitBuilder> fieldInit){
        this.fieldInits.add(fieldInit.apply(new FieldInitBuilder()));
        return this;
    }

}
