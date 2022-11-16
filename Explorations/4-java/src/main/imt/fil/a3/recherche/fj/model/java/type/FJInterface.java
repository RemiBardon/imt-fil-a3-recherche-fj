package imt.fil.a3.recherche.fj.model.java.type;

import imt.fil.a3.recherche.fj.model.TypeCheckingContext;
import imt.fil.a3.recherche.fj.model.TypeTable;
import imt.fil.a3.recherche.fj.model.java.misc.FJMethod;
import imt.fil.a3.recherche.fj.model.java.misc.FJSignature;
import imt.fil.a3.recherche.fj.util.haskell.Haskell;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record FJInterface(
    String name,
    List<String> extendsNames,
    List<FJSignature> signatures,
    List<FJMethod> defaultMethods
) implements FJType {

    @Override
    public Boolean typeCheck(final TypeCheckingContext context) {
        return context.typeTable.abstractMethods(this.name).isPresent()
            && this.defaultMethods.stream().allMatch(m -> m.typeCheck(context, this.name));
    }

    @Override
    public Boolean isSubtype(final TypeTable typeTable, final String otherTypeName) {
        return this.extendsNames.contains(otherTypeName)
            || this.extendsNames.stream().anyMatch(t -> typeTable.isSubtype(t, otherTypeName));
    }

    @Override
    public Optional<List<FJSignature>> abstractMethods(final TypeTable typeTable) {
        final Stream<FJSignature> superAbstractMethods = this.extendsNames.stream()
            .flatMap(i -> typeTable.abstractMethods(i).orElse(Collections.emptyList()).stream());

        final Stream<FJSignature> abstractMethods = Haskell.union(
            this.signatures.stream(),
            superAbstractMethods,
            (s1, s2) -> s1.name().equals(s2.name())
        );

        return Optional.of(abstractMethods.collect(Collectors.toList()));
    }

    @Override
    public Optional<List<FJMethod>> methods(final TypeTable typeTable) {
        final Stream<FJMethod> superMethods = this.extendsNames.stream()
            .flatMap(i -> typeTable.methods(i).orElse(Collections.emptyList()).stream());
        final Stream<FJMethod> methods = Haskell.union(
            this.defaultMethods.stream(),
            superMethods,
            FJMethod::signatureEquals
        );
        return Optional.of(methods.collect(Collectors.toList()));
    }
}
