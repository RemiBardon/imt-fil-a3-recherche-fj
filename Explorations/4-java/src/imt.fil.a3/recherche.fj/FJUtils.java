package imt.fil.a3.recherche.fj;

import imt.fil.a3.recherche.fj.parser.*;
import imt.fil.a3.recherche.fj.parser.expression.*;
import imt.fil.a3.recherche.fj.parser.type.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class FJUtils {

    public static Boolean isSubtype(
        final HashMap<String, FJType> classTable,
        final String classA,
        final String classB
    ) {
        if (classA.equals(classB)) return true;

        final FJType fjType = classTable.get(classA);

        if (fjType instanceof final FJClass fjClass) {
            if (fjClass.extendsName.equals(classB) || fjClass.implementsNames.contains(classB)) {
                return true;
            } else {
                return isSubtype(classTable, fjClass.extendsName, classB)
                    || fjClass.implementsNames.stream()
                        .anyMatch(implementName -> isSubtype(classTable, implementName, classB));
            }
        } else if (fjType instanceof final FJInterface fjInterface) {
            return fjInterface.extendsNames.contains(classB)
                || fjInterface.extendsNames.stream()
                    .anyMatch(implementName -> isSubtype(classTable, implementName, classB));
        } else {
            return false;
        }
    }

    public static Optional<List<FJField>> classFields(
        final HashMap<String, FJType> classTable,
        final String className
    ) {
        if (className.equals("Object")) return Optional.empty();

        FJType fjType = classTable.get(className);

        if (fjType instanceof final FJClass fjClass) {
            return classFields(classTable, fjClass.extendsName).map(fields -> {
                fields.addAll(fjClass.fields);
                return fields;
            });
        } else {
            return Optional.empty();
        }
    }

    public static Optional<List<FJSignature>> abstractMethods(
        final HashMap<String, FJType> classTable,
        final String className
    ) {
        if (className.equals("Object")) return Optional.empty();

        final FJType fjType = classTable.get(className);

        if (fjType instanceof final FJClass fjClass) {
            return abstractMethods(classTable,fjClass.extendsName)
                .map(superAbstractMethods -> {
                    final List<String> implementsAbstractMethods = fjClass.implementsNames.stream()
                        .map(implementName -> abstractMethods(classTable, implementName))
                        .flatMap(Optional::stream)
                        .flatMap(List::stream)
                        .map(FJSignature::toString)
                        .toList();

                    // union between superAbstractMethods and implementsAbstractMethods when they are in the same class
                    final List<FJSignature> abstractMethods = superAbstractMethods.stream()
                        .filter(superAbstractMethod -> implementsAbstractMethods.contains(superAbstractMethod.name))
                        .toList();

                    // final List<FJSignature> concreteMethods

                    throw new RuntimeException("Not implemented yet.");
                });
        }
        throw new RuntimeException("Not implemented yet.");
    }

    public static Optional<List<FJMethod>> methods(
        final HashMap<String, FJType> classTable,
        final String className
    ) {
        if (className.equals("Object")) return Optional.empty();

        final FJType fjType = classTable.get(className);

        if (fjType instanceof final FJClass fjClass) {
            return methods(classTable,fjClass.extendsName).map(methods -> {
                methods.addAll(fjClass.methods);
                return methods;
            });
        } else {
            return Optional.empty();
        }
    }

    public static Optional<FJMethodTypeSignature> methodType(
        final HashMap<String, FJType> classTable,
        final String methodName,
        final String className
    ) {
        throw new RuntimeException("Not implemented yet.");
    }

    public static Optional<FJMethodBodySignature> methodBody(
        final HashMap<String, FJType> classTable,
        final String methodName,
        final String className
    ) {
        throw new RuntimeException("Not implemented yet.");
    }
}
