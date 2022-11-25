package imt.fil.a3.recherche.fj.util.builder.model.type;

import imt.fil.a3.recherche.fj.model.java.misc.FJMethod;
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
    private final List<String> extendsNames = new ArrayList<>();
    private final List<FJSignatureBuilder> signatures = new ArrayList<>();
    private final List<FJMethodBuilder> defaultMethods = new ArrayList<>();
    private String name;

    @Override
    public FJInterface build() throws FJBuilderException {
        final List<FJSignature> signatures = new ArrayList<>();
        for (final FJSignatureBuilder s : this.signatures) {
            signatures.add(s.build());
        }
        final List<FJMethod> defaultMethods = new ArrayList<>();
        for (final FJMethodBuilder method : this.defaultMethods) {
            defaultMethods.add(method.build());
        }
        return new FJInterface(this.name, this.extendsNames, signatures, defaultMethods);
    }

    public FJInterfaceBuilder name(String name) {
        this.name = name;
        return this;
    }

    public FJInterfaceBuilder extendsName(String extendsName) {
        this.extendsNames.add(extendsName);
        return this;
    }

    public FJInterfaceBuilder signature(Function<FJSignatureBuilder, FJSignatureBuilder> signature) {
        this.signatures.add(signature.apply(new FJSignatureBuilder()));
        return this;
    }

    public FJInterfaceBuilder defaultMethod(Function<FJMethodBuilder, FJMethodBuilder> defaultMethod) {
        this.defaultMethods.add(defaultMethod.apply(new FJMethodBuilder()));
        return this;
    }
}
