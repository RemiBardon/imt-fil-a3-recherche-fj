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
    private List<FJClassBuilder> classes;
    private List<FJInterfaceBuilder> interfaces;

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

    public FJProgramBuilder clazz(Function<FJClassBuilder, FJClassBuilder> clazz) {
        this.classes.add(clazz.apply(new FJClassBuilder()));
        return this;
    }

    public FJProgramBuilder interfaze(Function<FJInterfaceBuilder, FJInterfaceBuilder> interfaze) {
        this.interfaces.add(interfaze.apply(new FJInterfaceBuilder()));
        return this;
    }

}
