package imt.fil.a3.recherche.fj.parser.type;

import imt.fil.a3.recherche.fj.parser.FJConstructor;
import imt.fil.a3.recherche.fj.parser.FJField;
import imt.fil.a3.recherche.fj.parser.FJMethod;

import java.util.List;

public final class FJClass implements FJType {
    public final String name;
    public final String extendsName;
    public final List<String> implementsNames;

    public final List<FJField> fields;
    public final List<FJMethod> methods;
    public final FJConstructor constructor;

    public FJClass(
        String name,
        String extendsName,
        List<String> implementsNames,
        List<FJField> fields,
        List<FJMethod> methods,
        FJConstructor constructor
    ) {
        this.name = name;
        this.extendsName = extendsName;
        this.implementsNames = implementsNames;
        this.fields = fields;
        this.methods = methods;
        this.constructor = constructor;
    }
}
