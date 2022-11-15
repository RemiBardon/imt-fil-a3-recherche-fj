package imt.fil.a3.recherche.fj;

import imt.fil.a3.recherche.fj.parser.*;
import imt.fil.a3.recherche.fj.parser.type.*;

import java.util.*;
import java.util.stream.Collectors;

public class FJUtils {

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
        signature = abstractMethods.get().stream().filter(m -> m.name.equals(methodName)).findFirst();
        if (signature.isPresent()) {
            return Optional.of(new FJMethodTypeSignature(
                signature.get().args.stream().map(f -> f.type).collect(Collectors.toList()),
                signature.get().returnTypeName
            ));
        }

        // Search in concrete methods
        final Optional<List<FJMethod>> methods = FJUtils.methods(classTable, typeName);
        if (methods.isEmpty()) return Optional.empty();
        signature = methods.get().stream().map(m -> m.signature).filter(m -> m.name.equals(methodName)).findFirst();
        // noinspection OptionalIsPresent
        if (signature.isPresent()) {
            return Optional.of(new FJMethodTypeSignature(
                signature.get().args.stream().map(f -> f.type).collect(Collectors.toList()),
                signature.get().returnTypeName
            ));
        }

        return Optional.empty();
    }

    public static Optional<FJMethodBodySignature> methodBody(
        final HashMap<String, FJType> classTable,
        final String methodName,
        final String className
    ) {
        if (className.equals("Object")) return Optional.empty();

        //get methods
        final Optional<List<FJMethod>> methods = methods(classTable, className);

        throw new RuntimeException("Not implemented yet.");

        /*
        //get method
        final Optional<FJMethod> method = methods.flatMap(methods1 -> methods1.stream()
                .filter(method1 -> method1.name.equals(methodName))
                .findFirst());

        //get method body
        return method.map(method1 -> new FJMethodBodySignature(
                method1.returnType,
                method1.arguments,
                method1.block
        ));
        */
    }
}
