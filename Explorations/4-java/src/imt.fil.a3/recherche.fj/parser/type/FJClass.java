package imt.fil.a3.recherche.fj.parser.type;

import imt.fil.a3.recherche.fj.FJUtils;
import imt.fil.a3.recherche.fj.haskell.Haskell;
import imt.fil.a3.recherche.fj.parser.FJConstructor;
import imt.fil.a3.recherche.fj.parser.FJField;
import imt.fil.a3.recherche.fj.parser.FJMethod;
import imt.fil.a3.recherche.fj.parser.FJSignature;

import java.util.*;
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
     *
     * @return {@code Boolean.TRUE} for a well-formed class, {@code Boolean.FALSE} otherwise.
     */
    public Boolean typeCheck(
        final HashMap<String, FJType> classTable,
        final HashMap<String, String> context
    ) {
        final FJConstructor constructor = this.constructor;

        // Get superclass fields or return false if not found.
        Optional<List<FJField>> superFields = FJUtils.classFields(classTable, this.extendsName);
        if (superFields.isEmpty()) return false;

        // Make sure all fields are passed to the constructor.
        List<FJField> allFields = Stream.concat(superFields.get().stream(), this.fields.stream())
            .collect(Collectors.toList());
        if (!constructor.args.equals(allFields)) return false;

        // Make sure constructor argument names match field names.
        if (constructor.fieldInits.stream().anyMatch(f -> !f.fieldName.equals(f.argumentName))) return false;

        final Optional<List<FJSignature>> abstractMethods = FJUtils.abstractMethods(classTable, this.name);
        if (abstractMethods.isEmpty()) return false; // Error obtaining abstract methods

        // Make sure all constructor arguments are used
        final List<String> args = constructor.args.stream().map(a -> a.name)
            .collect(Collectors.toList());
        final List<String> usedArgs = Stream.concat(
            constructor.superArgs.stream(),
            constructor.fieldInits.stream().map(fi -> fi.fieldName)
        ).collect(Collectors.toList());

        return abstractMethods.get().isEmpty()
            && (args.equals(usedArgs))
            && this.methods.stream().allMatch(m -> m.typeCheck(classTable, context, this.name));
    }

    @Override
    public Boolean isSubtype(final HashMap<String, FJType> classTable, final String otherTypeName) {
        if (this.extendsName.equals(otherTypeName) || this.implementsNames.contains(otherTypeName)) {
            return true;
        } else {
            return FJUtils.isSubtype(classTable, this.extendsName, otherTypeName)
                || this.implementsNames.stream().anyMatch(t -> FJUtils.isSubtype(classTable, t, otherTypeName));
        }
    }

    @Override
    public Optional<List<FJField>> classFields(final HashMap<String, FJType> classTable) {
        return FJUtils.classFields(classTable, this.extendsName).map(fields -> {
            List<FJField> res = new ArrayList<>(fields);
            res.addAll(this.fields);
            return res;
        });
    }

    @Override
    public Optional<List<FJSignature>> abstractMethods(final HashMap<String, FJType> classTable) {
        return FJUtils.abstractMethods(classTable, this.extendsName).map(superAbstractMethods -> {
            final Stream<FJSignature> implementsAbstractMethods = this.implementsNames.stream()
                .flatMap(t -> FJUtils.abstractMethods(classTable, t).orElse(Collections.emptyList()).stream());

            final Stream<FJSignature> abstractMethods = Haskell.union(
                superAbstractMethods.stream(),
                implementsAbstractMethods,
                (s1, s2) -> s1.name.equals(s2.name)
            );

            final Stream<FJSignature> concreteMethods;
            final Optional<List<FJMethod>> superMethods = FJUtils.methods(classTable, this.extendsName);
            if (superMethods.isPresent()) {
                final Optional<List<FJMethod>> methods = FJUtils.methods(classTable, this.name);
                // noinspection OptionalIsPresent
                if (methods.isPresent()) {
                    concreteMethods = Haskell.union(
                        superMethods.get().stream().map(m -> m.signature),
                        methods.get().stream().map(m -> m.signature),
                        (s1, s2) -> s1.name.equals(s2.name)
                    );
                } else {
                    concreteMethods = superMethods.get().stream().map(m -> m.signature);
                }
            } else {
                concreteMethods = Stream.empty();
            }

            return Haskell.difference(abstractMethods.toList(), concreteMethods.toList());
        });
    }

    @Override
    public Optional<List<FJMethod>> methods(final HashMap<String, FJType> classTable) {
        return FJUtils.methods(classTable, this.extendsName).map(superMethods -> {
            final Stream<FJMethod> thisPlusSuperMethods = Haskell.union(
                this.methods.stream(),
                superMethods.stream(),
                FJMethod::signatureEquals
            );
            final Stream<FJMethod> interfacesMethods = this.implementsNames.stream()
                .flatMap(t -> FJUtils.methods(classTable, t).orElse(Collections.emptyList()).stream());
            final Stream<FJMethod> allMethods = Haskell.union(
                thisPlusSuperMethods,
                interfacesMethods,
                FJMethod::signatureEquals
            );
            return allMethods.collect(Collectors.toList());
        });
    }
}
