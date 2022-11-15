package imt.fil.a3.recherche.fj.util;

import imt.fil.a3.recherche.fj.model.java.misc.FJField;
import imt.fil.a3.recherche.fj.model.java.misc.FJMethod;
import imt.fil.a3.recherche.fj.model.java.misc.FJSignature;
import imt.fil.a3.recherche.fj.model.java.type.FJType;
import imt.fil.a3.recherche.fj.model.misc.FJMethodBodySignature;
import imt.fil.a3.recherche.fj.model.misc.FJMethodTypeSignature;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public abstract class FJUtils {
    public static Boolean isSubtype(
        final HashMap<String, FJType> classTable,
        final String typeAName,
        final String typeBName
    ) {
        if (typeAName.equals(typeBName)) return true;
        if (!classTable.containsKey(typeAName)) return false;
        return classTable.get(typeAName).isSubtype(classTable, typeBName);
    }

    public static Optional<List<FJField>> classFields(
        final HashMap<String, FJType> classTable,
        final String typeName
    ) {
        if (typeName.equals("Object")) return Optional.empty();
        if (!classTable.containsKey(typeName)) return Optional.empty();
        return classTable.get(typeName).classFields(classTable);
    }

    public static Optional<FJMethodTypeSignature> methodType(
        final HashMap<String, FJType> classTable,
        final String methodName,
        final String typeName
    ) {
        if (typeName.equals("Object")) return Optional.empty();

        Optional<FJSignature> signature;

        // Search in abstract methods
        final Optional<List<FJSignature>> abstractMethods = FJUtils.abstractMethods(classTable, typeName);
        if (abstractMethods.isEmpty()) return Optional.empty();
        signature = abstractMethods.get().stream()
            .filter(m -> m.name().equals(methodName)).findFirst();
        if (signature.isPresent()) return Optional.of(signature.get().getTypeSignature());

        // Search in concrete methods
        final Optional<List<FJMethod>> methods = FJUtils.methods(classTable, typeName);
        if (methods.isEmpty()) return Optional.empty();
        signature = methods.get().stream().map(FJMethod::signature)
            .filter(m -> m.name().equals(methodName)).findFirst();
        return signature.map(FJSignature::getTypeSignature);
    }

    public static Optional<List<FJSignature>> abstractMethods(
        final HashMap<String, FJType> classTable,
        final String typeName
    ) {
        if (typeName.equals("Object")) return Optional.of(Collections.emptyList());
        if (!classTable.containsKey(typeName)) return Optional.empty();
        return classTable.get(typeName).abstractMethods(classTable);
    }

    public static Optional<List<FJMethod>> methods(
        final HashMap<String, FJType> classTable,
        final String typeName
    ) {
        if (typeName.equals("Object")) return Optional.of(Collections.emptyList());
        if (!classTable.containsKey(typeName)) return Optional.empty();
        return classTable.get(typeName).methods(classTable);
    }

    public static Optional<FJMethodBodySignature> methodBody(
        final HashMap<String, FJType> classTable,
        final String methodName,
        final String typeName
    ) {
        if (typeName.equals("Object")) return Optional.empty();

        final Optional<List<FJMethod>> methods = FJUtils.methods(classTable, typeName);
        if (methods.isEmpty()) return Optional.empty();

        return methods.get().stream()
            .filter(m -> m.signature().name().equals(methodName)).findFirst()
            .map(FJMethod::getBodySignature);
    }
}
