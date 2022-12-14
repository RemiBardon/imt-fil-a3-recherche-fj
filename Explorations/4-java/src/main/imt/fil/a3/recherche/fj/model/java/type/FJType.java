package imt.fil.a3.recherche.fj.model.java.type;

import imt.fil.a3.recherche.fj.model.TypeCheckingContext;
import imt.fil.a3.recherche.fj.model.TypeTable;
import imt.fil.a3.recherche.fj.model.error.ClassNotFound;
import imt.fil.a3.recherche.fj.model.java.misc.FJField;
import imt.fil.a3.recherche.fj.model.java.misc.FJMethod;
import imt.fil.a3.recherche.fj.model.java.misc.FJSignature;

import java.util.List;
import java.util.Optional;

public interface FJType {
    String name();

    /**
     * @param context The type checking context.
     * @return {@code Optional.empty()} if type check failed,
     * an annotated version of {@code this} if type check succeeded.
     */
    Optional<? extends FJType> typeCheckApproach1(final TypeCheckingContext context) throws ClassNotFound;

    Boolean typeCheckApproach2(final TypeCheckingContext context) throws ClassNotFound;

    Boolean isSubtype(final TypeTable typeTable, final String otherTypeName) throws ClassNotFound;

    default Optional<List<FJField>> classFields(final TypeTable typeTable) {
        return Optional.empty();
    }

    Optional<List<FJSignature>> abstractMethods(final TypeTable typeTable);

    Optional<List<FJMethod>> methods(final TypeTable typeTable);
}
