package imt.fil.a3.recherche.fj;

import imt.fil.a3.recherche.fj.parser.*;
import imt.fil.a3.recherche.fj.parser.type.*;

import java.util.*;

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
        final String className
    ) {
        if (className.equals("Object")) return Optional.empty();

        //get abstract methods
        final Optional<List<FJSignature>> abstractMethods = abstractMethods(classTable, className);

//get methods
        final Optional<List<FJMethod>> methods = methods(classTable, className);

        throw new RuntimeException("Not implemented yet.");

        /*
        //get method
        final Optional<FJMethod> method = methods.flatMap(methods1 -> methods1.stream()
                .filter(method1 -> method1.name.equals(methodName))
                .findFirst());

        //get method type
        return method.map(method1 -> new FJMethodTypeSignature(
                method1.returnType,
                method1.arguments.stream()
                        .map(argument -> argument.type)
                        .toList()
        ));
        */
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
