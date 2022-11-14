package imt.fil.a3.recherche.fj.parser.type;

import imt.fil.a3.recherche.fj.FJUtils;
import imt.fil.a3.recherche.fj.haskell.Haskell;
import imt.fil.a3.recherche.fj.parser.FJMethod;
import imt.fil.a3.recherche.fj.parser.FJSignature;

import javax.swing.text.html.Option;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Override
    public Optional<List<FJSignature>> abstractMethods(final HashMap<String, FJType> classTable) {
        final Stream<FJSignature> superAbstractMethods = this.extendsNames.stream()
            .flatMap(i -> FJUtils.abstractMethods(classTable, i).orElse(Collections.emptyList()).stream());

        final Stream<FJSignature> abstractMethods = Haskell.union(
            this.signatures.stream(),
            superAbstractMethods,
            (s1, s2) -> s1.name.equals(s2.name)
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
