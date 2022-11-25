package imt.fil.a3.recherche.fj.model.java.type;

import imt.fil.a3.recherche.fj.model.TypeCheckingContext;
import imt.fil.a3.recherche.fj.model.TypeTable;
import imt.fil.a3.recherche.fj.model.error.ClassNotFound;
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
    @Override
    public Optional<FJClass> typeCheckApproach1(final TypeCheckingContext context) throws ClassNotFound {
        final FJConstructor constructor = this.constructor;

        // Get superclass fields or return `Optional.empty()` if not found.
        final Optional<List<FJField>> superFields = context.typeTable.classFields(this.extendsName);
        if (superFields.isEmpty()) {
            TypeCheckingContext.logger.info("Superclass fields not found.");
            return Optional.empty();
        }

        // Make sure all fields are passed to the constructor.
        final List<FJField> allFields = Stream.concat(superFields.get().stream(), this.fields.stream())
            .collect(Collectors.toList());
        if (!constructor.args().equals(allFields)) {
            TypeCheckingContext.logger.info("Not all fields are passed to the constructor.");
            return Optional.empty();
        }

        // Make sure constructor argument names match field names.
        if (constructor.fieldInits().stream().anyMatch(f -> !f.fieldName().equals(f.argumentName()))) {
            TypeCheckingContext.logger.info("Constructor argument names do not match field names.");
            return Optional.empty();
        }

        final Optional<List<FJSignature>> abstractMethods = context.typeTable.abstractMethods(this.name);
        if (abstractMethods.isEmpty()) {
            TypeCheckingContext.logger.info("Error obtaining abstract methods.");
            return Optional.empty();
        }
        if (!abstractMethods.get().isEmpty()) {
            TypeCheckingContext.logger.info("Not all abstract methods are implemented.");
            return Optional.empty();
        }

        // Make sure all constructor arguments are used
        final List<String> args = constructor.args().stream().map(FJField::name)
            .collect(Collectors.toList());
        final List<String> usedArgs = Stream.concat(
            constructor.superArgs().stream(),
            constructor.fieldInits().stream().map(FieldInit::fieldName)
        ).collect(Collectors.toList());
        if (!args.equals(usedArgs)) {
            TypeCheckingContext.logger.info("Not all constructor arguments are used.");
            return Optional.empty();
        }

        // Make sure all methods are correctly typed
        //eq of: List<FJMethod> typedMethods = this.methods.stream()
        //            .map(m -> m.typeCheckApproach1(context, this.name))
        //            .flatMap(Optional::stream).toList();
        final List<FJMethod> typedMethods = new ArrayList<>();
        for (FJMethod method : this.methods) {
            method.typeCheckApproach1(context, this.name).map(typedMethods::add);
        }
        final boolean methodsAreTypedCorrectly = typedMethods.size() == this.methods.size();
        if (!methodsAreTypedCorrectly) {
            TypeCheckingContext.logger.info("Methods are not correctly typed.");
            return Optional.empty();
        }

        return Optional.of(new FJClass(
            this.name, this.extendsName, this.implementsNames, this.fields, typedMethods, this.constructor
        ));
    }

    @Override
    public Boolean typeCheckApproach2(final TypeCheckingContext context) throws ClassNotFound {
        final FJConstructor constructor = this.constructor;

        // Get superclass fields or return false if not found.
        final Optional<List<FJField>> superFields = context.typeTable.classFields(this.extendsName);
        if (superFields.isEmpty()) {
            TypeCheckingContext.logger.info("Superclass fields not found.");
            return false;
        }

        // Make sure all fields are passed to the constructor.
        final List<FJField> allFields = Stream.concat(superFields.get().stream(), this.fields.stream())
            .collect(Collectors.toList());
        if (!constructor.args().equals(allFields)) {
            TypeCheckingContext.logger.info("Not all fields are passed to the constructor.");
            return false;
        }

        // Make sure constructor argument names match field names.
        if (constructor.fieldInits().stream().anyMatch(f -> !f.fieldName().equals(f.argumentName()))) {
            TypeCheckingContext.logger.info("Constructor argument names do not match field names.");
            return false;
        }

        final Optional<List<FJSignature>> abstractMethods = context.typeTable.abstractMethods(this.name);
        if (abstractMethods.isEmpty()) {
            TypeCheckingContext.logger.info("Error obtaining abstract methods.");
            return false;
        }
        if (!abstractMethods.get().isEmpty()) {
            TypeCheckingContext.logger.info("Not all abstract methods are implemented.");
            return false;
        }

        // Make sure all constructor arguments are used
        final List<String> args = constructor.args().stream().map(FJField::name)
            .collect(Collectors.toList());
        final List<String> usedArgs = Stream.concat(
            constructor.superArgs().stream(),
            constructor.fieldInits().stream().map(FieldInit::fieldName)
        ).collect(Collectors.toList());
        if (!args.equals(usedArgs)) {
            TypeCheckingContext.logger.info("Not all constructor arguments are used.");
            return false;
        }

        // Make sure all methods are correctly typed
        //eq of: final boolean methodsAreTypedCorrectly = this.methods.stream()
        //            .allMatch(m -> m.typeCheckApproach2(context, this.name));
        boolean methodsAreTypedCorrectly = true;

        for (FJMethod method : this.methods) {
            methodsAreTypedCorrectly &= method.typeCheckApproach2(context, this.name);
        }


        if (!methodsAreTypedCorrectly) {
            TypeCheckingContext.logger.info("Methods are not correctly typed.");
            return false;
        }

        return true;
    }

    @Override
    public Boolean isSubtype(final TypeTable typeTable, final String otherTypeName) throws ClassNotFound {
        if (this.extendsName.equals(otherTypeName) || this.implementsNames.contains(otherTypeName)) {
            return true;
        } else {
            //eq of: typeTable.isSubtype(this.extendsName, otherTypeName) || this.implementsNames.stream().anyMatch(t -> typeTable.isSubtype(t, otherTypeName));
            if(typeTable.isSubtype(this.extendsName, otherTypeName)){
                return true;
            }
            boolean isSubType = true;
            for (String implementsName : this.implementsNames) {
                isSubType &= !typeTable.isSubtype(implementsName, otherTypeName);
            }
            return isSubType;
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
            final List<FJSignature> implementsAbstractMethods = this.implementsNames.stream()
                .flatMap(t -> typeTable.abstractMethods(t).orElse(Collections.emptyList()).stream())
                .toList();

            final List<FJSignature> abstractMethods = Haskell.union(
                superAbstractMethods,
                implementsAbstractMethods,
                (s1, s2) -> s1.name().equals(s2.name())
            );

            final List<FJSignature> concreteMethods;
            final Optional<List<FJMethod>> superMethods = typeTable.methods(this.extendsName);
            if (superMethods.isPresent()) {
                final Optional<List<FJMethod>> methods = typeTable.methods(this.name);
                if (methods.isPresent()) {
                    concreteMethods = Haskell.union(
                        superMethods.get().stream().map(FJMethod::signature).toList(),
                        methods.get().stream().map(FJMethod::signature).toList(),
                        (s1, s2) -> s1.name().equals(s2.name())
                    );
                } else {
                    concreteMethods = superMethods.get().stream().map(FJMethod::signature).toList();
                }
            } else {
                concreteMethods = Collections.emptyList();
            }

            return Haskell.difference(abstractMethods, concreteMethods);
        });
    }

    @Override
    public Optional<List<FJMethod>> methods(final TypeTable typeTable) {
        return typeTable.methods(this.extendsName).map(superMethods -> {
            final List<FJMethod> thisPlusSuperMethods = Haskell.union(
                this.methods,
                superMethods,
                FJMethod::signatureEquals
            );
            final List<FJMethod> interfacesMethods = this.implementsNames.stream()
                .flatMap(t -> typeTable.methods(t).orElse(Collections.emptyList()).stream())
                .toList();
            return Haskell.union(
                thisPlusSuperMethods,
                interfacesMethods,
                FJMethod::signatureEquals
            );
        });
    }
}
