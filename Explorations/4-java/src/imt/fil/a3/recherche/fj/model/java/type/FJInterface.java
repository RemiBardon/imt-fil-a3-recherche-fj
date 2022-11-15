package imt.fil.a3.recherche.fj.model.java.type;

import imt.fil.a3.recherche.fj.model.java.misc.FJMethod;
import imt.fil.a3.recherche.fj.model.java.misc.FJSignature;
import imt.fil.a3.recherche.fj.util.FJUtils;
import imt.fil.a3.recherche.fj.util.haskell.Haskell;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record FJInterface(
    String name, List<String> extendsNames,
    List<FJMethod> methods,
    List<FJSignature> signatures,
    List<FJMethod> defaultMethods
) implements FJType {
    /**
     * Checks if an interface is well-formed.
     *
     * @return {@code Boolean.TRUE} for a well-formed interface, {@code Boolean.FALSE} otherwise.
     */
    public Boolean typeCheck(
        final HashMap<String, FJType> classTable,
        final HashMap<String, String> context
    ) {
        return FJUtils.abstractMethods(classTable, this.name).isPresent()
            && this.defaultMethods.stream().allMatch(m -> m.typeCheck(classTable, context, this.name));
    }

    @Override
    public Boolean isSubtype(final HashMap<String, FJType> classTable, final String otherTypeName) {
        return this.extendsNames.contains(otherTypeName)
            || this.extendsNames.stream().anyMatch(t -> FJUtils.isSubtype(classTable, t, otherTypeName));
    }

    @Override
    public Optional<List<FJSignature>> abstractMethods(final HashMap<String, FJType> classTable) {
        final Stream<FJSignature> superAbstractMethods = this.extendsNames.stream()
            .flatMap(i -> FJUtils.abstractMethods(classTable, i).orElse(Collections.emptyList()).stream());

        final Stream<FJSignature> abstractMethods = Haskell.union(
            this.signatures.stream(),
            superAbstractMethods,
            (s1, s2) -> s1.name().equals(s2.name())
        );

        return Optional.of(abstractMethods.collect(Collectors.toList()));
    }

    @Override
    public Optional<List<FJMethod>> methods(final HashMap<String, FJType> classTable) {
        final Stream<FJMethod> superMethods = this.extendsNames.stream()
            .flatMap(i -> FJUtils.methods(classTable, i).orElse(Collections.emptyList()).stream());
        final Stream<FJMethod> methods = Haskell.union(
            this.defaultMethods.stream(),
            superMethods,
            FJMethod::signatureEquals
        );
        return Optional.of(methods.collect(Collectors.toList()));
    }
}
