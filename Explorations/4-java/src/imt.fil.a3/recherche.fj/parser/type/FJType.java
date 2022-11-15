package imt.fil.a3.recherche.fj.parser.type;

import imt.fil.a3.recherche.fj.parser.FJMethod;
import imt.fil.a3.recherche.fj.parser.FJSignature;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public interface FJType {
    Boolean isSubtype(final HashMap<String, FJType> classTable, final String otherTypeName);
    Optional<List<FJSignature>> abstractMethods(final HashMap<String, FJType> classTable);
    Optional<List<FJMethod>> methods(final HashMap<String, FJType> classTable);
}
