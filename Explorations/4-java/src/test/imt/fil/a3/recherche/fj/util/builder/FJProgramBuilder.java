package imt.fil.a3.recherche.fj.util.builder;

import imt.fil.a3.recherche.fj.model.java.misc.FJProgram;
import imt.fil.a3.recherche.fj.model.java.type.FJClass;
import imt.fil.a3.recherche.fj.model.java.type.FJInterface;
import imt.fil.a3.recherche.fj.model.java.type.FJType;
import imt.fil.a3.recherche.fj.util.builder.error.FJBuilderException;
import imt.fil.a3.recherche.fj.util.builder.model.type.FJClassBuilder;
import imt.fil.a3.recherche.fj.util.builder.model.type.FJInterfaceBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class FJProgramBuilder implements FJBuilder<FJProgram> {
    private final List<FJClassBuilder> classes = new ArrayList<>();
    private final List<FJInterfaceBuilder> interfaces = new ArrayList<>();

    @Override
    public FJProgram build() throws FJBuilderException {
        List<FJInterface> fjInterfaces = new ArrayList<>();
        List<FJClass> fjClasses = new ArrayList<>();
        List<FJType> fjTypes = new ArrayList<>();

        for (FJInterfaceBuilder fjInterfaceBuilder : this.interfaces) {
            fjInterfaces.add(fjInterfaceBuilder.build());
        }

        for (FJClassBuilder fjClassBuilder : this.classes) {
            fjClasses.add(fjClassBuilder.build());
        }

        fjTypes.addAll(fjInterfaces);
        fjTypes.addAll(fjClasses);

        return new FJProgram(fjTypes);
    }

    public FJProgramBuilder clazz(Function<FJClassBuilder, FJClassBuilder> update) {
        return this.clazz(update.apply(new FJClassBuilder()));
    }

    public FJProgramBuilder clazz(FJClassBuilder clazz) {
        this.classes.add(clazz);
        return this;
    }

    public FJProgramBuilder interfaze(Function<FJInterfaceBuilder, FJInterfaceBuilder> update) {
        return this.interfaze(update.apply(new FJInterfaceBuilder()));
    }

    public FJProgramBuilder interfaze(FJInterfaceBuilder interfaze) {
        this.interfaces.add(interfaze);
        return this;
    }

}
