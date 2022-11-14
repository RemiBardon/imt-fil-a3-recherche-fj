package imt.fil.a3.recherche.fj;

import imt.fil.a3.recherche.fj.parser.*;
import imt.fil.a3.recherche.fj.parser.type.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        if (className.equals("Object")) return Optional.of(Collections.emptyList());

        if (!classTable.containsKey(className)) return Optional.empty();

        final FJType fjType = classTable.get(className);

        if (fjType instanceof final FJClass fjClass) {
            return abstractMethods(classTable, fjClass.extendsName).map(superAbstractMethods -> {
                final Stream<FJSignature> implementsAbstractMethods = fjClass.implementsNames.stream()
                    .flatMap(t -> abstractMethods(classTable, t).orElse(Collections.emptyList()).stream());

                final Stream<FJSignature> abstractMethods = Haskell.union(
                    superAbstractMethods.stream(),
                    implementsAbstractMethods,
                    (s1, s2) -> s1.name.equals(s2.name)
                );

                final Stream<FJSignature> concreteMethods;
                final Optional<List<FJMethod>> superMethods = methods(classTable, fjClass.extendsName);
                if (superMethods.isPresent()) {
                    final Optional<List<FJMethod>> methods = methods(classTable, fjClass.name);
                    // noinspection OptionalIsPresent
                    if (methods.isPresent()) {
                        concreteMethods = Haskell.union(
                            superMethods.get().stream().map(m -> m.signature),
                            methods.get().stream().map(m -> m.signature),
                            (s1, s2) -> s1.name.equals(s2.name)
                        );
                    } else {
                        concreteMethods = superMethods.get().stream().map(m -> m.signature);
                    }
                } else {
                    concreteMethods = Stream.empty();
                }

                return Haskell.difference(abstractMethods.toList(), concreteMethods.toList());
            });
        } else if (fjType instanceof final FJInterface fjInterface) {
            final Stream<FJSignature> superAbstractMethods = fjInterface.extendsNames.stream()
                .flatMap(i -> abstractMethods(classTable, i).orElse(Collections.emptyList()).stream());

            final Stream<FJSignature> abstractMethods = Haskell.union(
                fjInterface.signatures.stream(),
                superAbstractMethods,
                (s1, s2) -> s1.name.equals(s2.name)
            );

            return Optional.of(abstractMethods.collect(Collectors.toList()));
        } else {
            throw new RuntimeException("Unexpected code path: expression type not supported.");
        }
    }

    public static Optional<List<FJMethod>> methods(
        final HashMap<String, FJType> classTable,
        final String className
    ) {
        if (className.equals("Object")) return Optional.empty();

        final FJType fjType = classTable.get(className);

        if (fjType instanceof final FJClass fjClass) {
           //get methods from implemented interfaces
            final Optional<List<FJMethod>> methodsFromImplemnts = fjClass.implementsNames.stream()
                    .map(implementName -> methods(classTable, implementName))
                    .flatMap(Optional::stream)
                    .reduce((methods1, methods2) -> {
                        methods1.addAll(methods2);
                        return methods1;
                    });

            //get methods from class
            final Optional<List<FJMethod>> methodsFromClass = Optional.of(fjClass.methods);

            //merge all methods
            return methodsFromImplemnts.map(methods -> {
                methodsFromClass.ifPresent(methods::addAll);
                return methods;
            });
        } else if (fjType instanceof final FJInterface fjInterface) {
            //get super methods
            final Optional<List<FJMethod>> methodsFromSuper = methods(classTable, className);

            //get methods from interface
            final Optional<List<FJMethod>> methodsFromInterface = Optional.of(fjInterface.methods);

            //merge all methods
            return methodsFromSuper.map(methods -> {
                methodsFromInterface.ifPresent(methods::addAll);
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
        if (className.equals("Object")) return Optional.empty();

        //get abstract methods
        final Optional<List<FJSignature>> abstractMethods = abstractMethods(classTable, className);

//get methods
        final Optional<List<FJMethod>> methods = methods(classTable, className);

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
    }

    public static Optional<FJMethodBodySignature> methodBody(
        final HashMap<String, FJType> classTable,
        final String methodName,
        final String className
    ) {
        if (className.equals("Object")) return Optional.empty();

        //get methods
        final Optional<List<FJMethod>> methods = methods(classTable, className);

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
    }
}
