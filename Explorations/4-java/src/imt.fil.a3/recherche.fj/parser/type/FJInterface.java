package imt.fil.a3.recherche.fj.parser.type;

import imt.fil.a3.recherche.fj.FJUtils;
import imt.fil.a3.recherche.fj.parser.FJMethod;
import imt.fil.a3.recherche.fj.parser.FJSignature;

import java.util.HashMap;
import java.util.List;

public final class FJInterface implements FJType {
    public final String name;
    public final List<String> extendsNames;

    public final List<FJMethod> methods;
    public final List<FJSignature> signatures;
    public final List<FJMethod> defaultMethods;

    public FJInterface(
            String name,
            List<String> extendsNames,
            List<FJMethod> methods,
            List<FJSignature> signatures,
            List<FJMethod> defaultMethods
    ) {
        this.name = name;
        this.extendsNames = extendsNames;
        this.methods = methods;
        this.signatures = signatures;
        this.defaultMethods = defaultMethods;
    }

    /**
     * Checks if an interface is well-formed.
     * @return {@code Boolean.TRUE} for a well-formed interface, {@code Boolean.FALSE} otherwise.
     */
    public Boolean typeCheck(
        final HashMap<String, FJType> classTable,
        final HashMap<String, String> context
    ) {
        return FJUtils.abstractMethods(classTable, this.name).isPresent()
            && this.defaultMethods.stream().allMatch(m -> m.typeCheck(classTable, context, this.name));
    }
}
