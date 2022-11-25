package imt.fil.a3.recherche.fj.model;

import imt.fil.a3.recherche.fj.model.error.ClassNotFound;
import imt.fil.a3.recherche.fj.model.java.misc.FJField;
import imt.fil.a3.recherche.fj.model.java.misc.FJMethod;
import imt.fil.a3.recherche.fj.model.java.misc.FJSignature;
import imt.fil.a3.recherche.fj.model.java.type.FJType;
import imt.fil.a3.recherche.fj.model.misc.MethodBodySignature;
import imt.fil.a3.recherche.fj.model.misc.MethodTypeSignature;

import java.util.*;

@SuppressWarnings("ClassCanBeRecord")
public final class TypeTable {
    private final Map<String, FJType> typeTable;

    public TypeTable(final Map<String, FJType> typeTable) {
        this.typeTable = typeTable;
    }

    public TypeTable copy() {
        return new TypeTable(new HashMap<>(this.typeTable));
    }

    public void add(final FJType type) {
        this.typeTable.put(type.name(), type);
    }

    public Boolean isSubtype(final String typeAName, final String typeBName) throws ClassNotFound {
        if (typeAName.equals("Object")) return true;
        if (!this.typeTable.containsKey(typeAName)) throw new ClassNotFound(typeAName);
        if (typeBName.equals("Object")) return true;
        if (!this.typeTable.containsKey(typeBName)) throw new ClassNotFound(typeBName);
        if (typeAName.equals(typeBName)) return true;
        return this.typeTable.get(typeAName).isSubtype(this, typeBName);
    }

    public Optional<List<FJField>> classFields(
        final String typeName
    ) {
        if (typeName.equals("Object")) return Optional.of(Collections.emptyList());
        if (!this.typeTable.containsKey(typeName)) return Optional.empty();
        return this.typeTable.get(typeName).classFields(this);
    }

    public Optional<MethodTypeSignature> methodType(
        final String methodName,
        final String typeName
    ) {
        if (typeName.equals("Object")) return Optional.empty();

        Optional<FJSignature> signature;

        // Search in abstract methods
        final Optional<List<FJSignature>> abstractMethods = this.abstractMethods(typeName);
        if (abstractMethods.isEmpty()) return Optional.empty();
        signature = abstractMethods.get().stream()
            .filter(m -> m.name().equals(methodName)).findFirst();
        if (signature.isPresent()) return Optional.of(signature.get().getTypeSignature());

        // Search in concrete methods
        final Optional<List<FJMethod>> methods = this.methods(typeName);
        if (methods.isEmpty()) return Optional.empty();
        signature = methods.get().stream().map(FJMethod::signature)
            .filter(m -> m.name().equals(methodName)).findFirst();
        return signature.map(FJSignature::getTypeSignature);
    }

    public Optional<List<FJSignature>> abstractMethods(
        final String typeName
    ) {
        if (typeName.equals("Object")) return Optional.of(Collections.emptyList());
        if (!this.typeTable.containsKey(typeName)) return Optional.empty();
        return this.typeTable.get(typeName).abstractMethods(this);
    }

    public Optional<List<FJMethod>> methods(
        final String typeName
    ) {
        if (typeName.equals("Object")) return Optional.of(Collections.emptyList());
        if (!this.typeTable.containsKey(typeName)) return Optional.empty();
        return this.typeTable.get(typeName).methods(this);
    }

    public Optional<MethodBodySignature> methodBody(
        final String methodName,
        final String typeName
    ) {
        if (typeName.equals("Object")) return Optional.empty();

        final Optional<List<FJMethod>> methods = this.methods(typeName);
        if (methods.isEmpty()) return Optional.empty();

        return methods.get().stream()
            .filter(m -> m.signature().name().equals(methodName)).findFirst()
            .map(FJMethod::getBodySignature);
    }
}
