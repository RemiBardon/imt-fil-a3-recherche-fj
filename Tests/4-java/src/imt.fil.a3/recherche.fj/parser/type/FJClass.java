package imt.fil.a3.recherche.fj.parser.type;

import imt.fil.a3.recherche.fj.FJUtils;
import imt.fil.a3.recherche.fj.parser.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    /**
     * Checks if a class is well-formed.
     * @return {@code Boolean.TRUE} for a well-formed class, {@code Boolean.FALSE} otherwise.
     */
    public Boolean typeCheck(
        final HashMap<String, FJType> classTable,
        final HashMap<String, String> context
    ) {
        final FJConstructor constructor = this.constructor;

        // Get superclass fields or return false if not found.
        Optional<List<FJField>> _superFields = FJUtils.classFields(classTable, this.extendsName);
        if (_superFields.isEmpty()) {
            return false;
        }
        List<FJField> superFields = _superFields.get();

        // Make sure all fields are passed to the constructor.
        List<FJField> allFields = Stream.concat(superFields.stream(), this.fields.stream())
            .collect(Collectors.toList());
        if (!constructor.args.equals(allFields)) {
            return false;
        }

        // Make sure constructor argument names match field names.
        if (constructor.fieldInits.stream().anyMatch(f -> !f.fieldName.equals(f.argumentName))) {
            return false;
        }

        final Optional<List<FJSignature>> _abstractMethods =
                FJUtils.abstractMethods(classTable, this.name);
        if (_abstractMethods.isEmpty()) {
            return false; // Error obtaining abstract methods
        }
        final List<FJSignature> abstractMethods = _abstractMethods.get();

        // Make sure all constructor arguments are used
        final List<String> args = constructor.args.stream().map(a -> a.name)
            .collect(Collectors.toList());
        final List<String> usedArgs = Stream.concat(
            constructor.superArgs.stream(),
            constructor.fieldInits.stream().map(fi -> fi.fieldName)
        ).collect(Collectors.toList());

        return abstractMethods.isEmpty()
            && (args.equals(usedArgs))
            && this.methods.stream().allMatch(m -> m.typeCheck(classTable, context, this.name));
    }
}
