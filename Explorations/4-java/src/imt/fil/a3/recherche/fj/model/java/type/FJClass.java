package imt.fil.a3.recherche.fj.model.java.type;

import imt.fil.a3.recherche.fj.model.TypeTable;
import imt.fil.a3.recherche.fj.model.java.misc.FJConstructor;
import imt.fil.a3.recherche.fj.model.java.misc.FJField;
import imt.fil.a3.recherche.fj.model.java.misc.FJMethod;
import imt.fil.a3.recherche.fj.model.java.misc.FJSignature;
import imt.fil.a3.recherche.fj.model.misc.FieldInit;
import imt.fil.a3.recherche.fj.util.haskell.Haskell;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record FJClass(
    String name,
    String extendsName,
    List<String> implementsNames,
    List<FJField> fields,
    List<FJMethod> methods,
    FJConstructor constructor
) implements FJType {
    /**
     * Checks if a class is well-formed.
     *
     * @return {@code Boolean.TRUE} for a well-formed class, {@code Boolean.FALSE} otherwise.
     */
    public Boolean typeCheck(
        final TypeTable typeTable,
        final HashMap<String, String> context
    ) {
        final FJConstructor constructor = this.constructor;

        // Get superclass fields or return false if not found.
        Optional<List<FJField>> superFields = typeTable.classFields(this.extendsName);
        if (superFields.isEmpty()) return false;

        // Make sure all fields are passed to the constructor.
        List<FJField> allFields = Stream.concat(superFields.get().stream(), this.fields.stream())
            .collect(Collectors.toList());
        if (!constructor.args().equals(allFields)) return false;

        // Make sure constructor argument names match field names.
        if (constructor.fieldInits().stream().anyMatch(f -> !f.fieldName().equals(f.argumentName()))) return false;

        final Optional<List<FJSignature>> abstractMethods = typeTable.abstractMethods(this.name);
        if (abstractMethods.isEmpty()) return false; // Error obtaining abstract methods

        // Make sure all constructor arguments are used
        final List<String> args = constructor.args().stream().map(FJField::name)
            .collect(Collectors.toList());
        final List<String> usedArgs = Stream.concat(
            constructor.superArgs().stream(),
            constructor.fieldInits().stream().map(FieldInit::fieldName)
        ).collect(Collectors.toList());

        return abstractMethods.get().isEmpty()
            && (args.equals(usedArgs))
            && this.methods.stream().allMatch(m -> m.typeCheck(typeTable, context, this.name));
    }

    @Override
    public Boolean isSubtype(final TypeTable typeTable, final String otherTypeName) {
        if (this.extendsName.equals(otherTypeName) || this.implementsNames.contains(otherTypeName)) {
            return true;
        } else {
            return typeTable.isSubtype(this.extendsName, otherTypeName)
                || this.implementsNames.stream().anyMatch(t -> typeTable.isSubtype(t, otherTypeName));
        }
    }

    @Override
    public Optional<List<FJField>> classFields(final TypeTable typeTable) {
        return typeTable.classFields(this.extendsName).map(fields -> {
            List<FJField> res = new ArrayList<>(fields);
            res.addAll(this.fields);
            return res;
        });
    }

    @Override
    public Optional<List<FJSignature>> abstractMethods(final TypeTable typeTable) {
        return typeTable.abstractMethods(this.extendsName).map(superAbstractMethods -> {
            final Stream<FJSignature> implementsAbstractMethods = this.implementsNames.stream()
                .flatMap(t -> typeTable.abstractMethods(t).orElse(Collections.emptyList()).stream());

            final Stream<FJSignature> abstractMethods = Haskell.union(
                superAbstractMethods.stream(),
                implementsAbstractMethods,
                (s1, s2) -> s1.name().equals(s2.name())
            );

            final Stream<FJSignature> concreteMethods;
            final Optional<List<FJMethod>> superMethods = typeTable.methods(this.extendsName);
            if (superMethods.isPresent()) {
                final Optional<List<FJMethod>> methods = typeTable.methods(this.name);
                // noinspection OptionalIsPresent
                if (methods.isPresent()) {
                    concreteMethods = Haskell.union(
                        superMethods.get().stream().map(FJMethod::signature),
                        methods.get().stream().map(FJMethod::signature),
                        (s1, s2) -> s1.name().equals(s2.name())
                    );
                } else {
                    concreteMethods = superMethods.get().stream().map(FJMethod::signature);
                }
            } else {
                concreteMethods = Stream.empty();
            }

            return Haskell.difference(abstractMethods.toList(), concreteMethods.toList());
        });
    }

    @Override
    public Optional<List<FJMethod>> methods(final TypeTable typeTable) {
        return typeTable.methods(this.extendsName).map(superMethods -> {
            final Stream<FJMethod> thisPlusSuperMethods = Haskell.union(
                this.methods.stream(),
                superMethods.stream(),
                FJMethod::signatureEquals
            );
            final Stream<FJMethod> interfacesMethods = this.implementsNames.stream()
                .flatMap(t -> typeTable.methods(t).orElse(Collections.emptyList()).stream());
            final Stream<FJMethod> allMethods = Haskell.union(
                thisPlusSuperMethods,
                interfacesMethods,
                FJMethod::signatureEquals
            );
            return allMethods.collect(Collectors.toList());
        });
    }
}
