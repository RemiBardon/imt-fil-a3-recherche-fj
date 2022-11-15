package imt.fil.a3.recherche.fj.model.java.type;

import imt.fil.a3.recherche.fj.model.java.misc.FJField;
import imt.fil.a3.recherche.fj.model.java.misc.FJMethod;
import imt.fil.a3.recherche.fj.model.java.misc.FJSignature;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public interface FJType {
    Boolean isSubtype(final HashMap<String, FJType> classTable, final String otherTypeName);

    default Optional<List<FJField>> classFields(final HashMap<String, FJType> classTable) {
        return Optional.empty();
    }

    Optional<List<FJSignature>> abstractMethods(final HashMap<String, FJType> classTable);

    Optional<List<FJMethod>> methods(final HashMap<String, FJType> classTable);
}
