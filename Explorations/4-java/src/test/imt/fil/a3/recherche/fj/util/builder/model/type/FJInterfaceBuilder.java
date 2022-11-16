package imt.fil.a3.recherche.fj.util.builder.model.type;

import imt.fil.a3.recherche.fj.model.java.misc.FJSignature;
import imt.fil.a3.recherche.fj.model.java.type.FJInterface;
import imt.fil.a3.recherche.fj.util.builder.FJBuilder;
import imt.fil.a3.recherche.fj.util.builder.error.FJBuilderException;
import imt.fil.a3.recherche.fj.util.builder.model.misc.FJMethodBuilder;
import imt.fil.a3.recherche.fj.util.builder.model.misc.FJSignatureBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class FJInterfaceBuilder implements FJBuilder<FJInterface> {

    private String name;
    private List<String> extendsNames = new ArrayList<>();
    private List<FJMethodBuilder> methods = new ArrayList<>();
    private List<FJSignatureBuilder> signatures = new ArrayList<>();
    private List<FJMethodBuilder> defaultMethods = new ArrayList<>();

    @Override
    public FJInterface build() throws FJBuilderException {
        throw new RuntimeException();
    }

    public FJInterfaceBuilder name(String name){
        this.name = name;
        return this;
    }

    public FJInterfaceBuilder extendsName(List<String> extendsNames){
        this.extendsNames = extendsNames;
        return this;
    }

    public FJInterfaceBuilder method(Function<FJMethodBuilder, FJMethodBuilder> method){
        this.methods.add(method.apply(new FJMethodBuilder()));
        return this;
    }

    public FJInterfaceBuilder signature(Function<FJSignatureBuilder, FJSignatureBuilder> signature){
        this.signatures.add(signature.apply(new FJSignatureBuilder()));
        return this;
    }

    public FJInterfaceBuilder defaultMethod(Function<FJMethodBuilder, FJMethodBuilder> defaultMethod){
        this.defaultMethods.add(defaultMethod.apply(new FJMethodBuilder()));
        return this;
    }
}
