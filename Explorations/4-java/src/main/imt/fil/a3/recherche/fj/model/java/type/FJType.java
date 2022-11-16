package imt.fil.a3.recherche.fj.model.java.type;

import imt.fil.a3.recherche.fj.model.TypeTable;
import imt.fil.a3.recherche.fj.model.java.misc.FJField;
import imt.fil.a3.recherche.fj.model.java.misc.FJMethod;
import imt.fil.a3.recherche.fj.model.java.misc.FJSignature;

import java.util.List;
import java.util.Optional;

public interface FJType {
    Boolean isSubtype(final TypeTable typeTable, final String otherTypeName);

    default Optional<List<FJField>> classFields(final TypeTable typeTable) {
        return Optional.empty();
    }

    Optional<List<FJSignature>> abstractMethods(final TypeTable typeTable);

    Optional<List<FJMethod>> methods(final TypeTable typeTable);
}
