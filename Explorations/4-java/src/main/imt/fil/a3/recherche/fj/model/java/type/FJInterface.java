package imt.fil.a3.recherche.fj.model.java.type;

import imt.fil.a3.recherche.fj.model.TypeCheckingContext;
import imt.fil.a3.recherche.fj.model.TypeTable;
import imt.fil.a3.recherche.fj.model.java.misc.FJMethod;
import imt.fil.a3.recherche.fj.model.java.misc.FJSignature;
import imt.fil.a3.recherche.fj.util.haskell.Haskell;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public record FJInterface(
    String name,
    List<String> extendsNames,
    List<FJSignature> signatures,
    List<FJMethod> defaultMethods
) implements FJType {
    @Override
    public Optional<FJInterface> typeCheckApproach1(final TypeCheckingContext context) {
        final Optional<List<FJSignature>> abstractMethods = context.typeTable.abstractMethods(this.name);
        if (abstractMethods.isEmpty()) {
            TypeCheckingContext.logger.warning("Interface not found in the type table.");
            return Optional.empty();
        }
        if (abstractMethods.get().isEmpty()) {
            TypeCheckingContext.logger.info("The interface does not have any abstract method.");
            return Optional.empty();
        }
        final List<FJMethod> typedDefaultMethods = this.defaultMethods.stream()
            .map(m -> m.typeCheckApproach1(context, this.name))
            .flatMap(Optional::stream).toList();
        if (typedDefaultMethods.size() != this.defaultMethods.size()) {
            TypeCheckingContext.logger.info("Not all default methods are correctly typed.");
            return Optional.empty();
        }
        return Optional.of(new FJInterface(this.name, this.extendsNames, this.signatures, typedDefaultMethods));
    }

    @Override
    public Boolean typeCheckApproach2(final TypeCheckingContext context) {
        final Optional<List<FJSignature>> abstractMethods = context.typeTable.abstractMethods(this.name);
        if (abstractMethods.isEmpty()) {
            TypeCheckingContext.logger.warning("Interface not found in the type table.");
            return false;
        }
        if (abstractMethods.get().isEmpty()) {
            TypeCheckingContext.logger.info("The interface does not have any abstract method.");
            return false;
        }
        if (!this.defaultMethods.stream().allMatch(m -> m.typeCheckApproach2(context, this.name))) {
            TypeCheckingContext.logger.info("Not all default methods are correctly typed.");
            return false;
        }
        return true;
    }

    @Override
    public Boolean isSubtype(final TypeTable typeTable, final String otherTypeName) {
        return this.extendsNames.contains(otherTypeName)
            || this.extendsNames.stream().anyMatch(t -> typeTable.isSubtype(t, otherTypeName));
    }

    @Override
    public Optional<List<FJSignature>> abstractMethods(final TypeTable typeTable) {
        final List<FJSignature> superAbstractMethods = this.extendsNames.stream()
            .flatMap(i -> typeTable.abstractMethods(i).orElse(Collections.emptyList()).stream())
            .toList();

        final List<FJSignature> abstractMethods = Haskell.union(
            this.signatures,
            superAbstractMethods,
            (s1, s2) -> s1.name().equals(s2.name())
        );

        return Optional.of(abstractMethods);
    }

    @Override
    public Optional<List<FJMethod>> methods(final TypeTable typeTable) {
        final List<FJMethod> superMethods = this.extendsNames.stream()
            .flatMap(i -> typeTable.methods(i).orElse(Collections.emptyList()).stream())
            .toList();
        final List<FJMethod> methods = Haskell.union(
            this.defaultMethods,
            superMethods,
            FJMethod::signatureEquals
        );
        return Optional.of(methods);
    }
}
