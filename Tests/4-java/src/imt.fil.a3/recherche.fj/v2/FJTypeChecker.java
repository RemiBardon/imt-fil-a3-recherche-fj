package imt.fil.a3.recherche.fj.v2;

import imt.fil.a3.recherche.fj.FJUtils;
import imt.fil.a3.recherche.fj.parser.*;
import imt.fil.a3.recherche.fj.parser.error.*;
import imt.fil.a3.recherche.fj.parser.expression.*;
import imt.fil.a3.recherche.fj.parser.type.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class FJTypeChecker {
    final HashMap<String, FJType> classTable;
    final HashMap<String, String> context;

    public FJTypeChecker(HashMap<String, FJType> classTable, HashMap<String, String> context) {
        this.classTable = classTable;
        this.context = context;
    }

    /**
     * Checks the type of a given expression.
     * @return The type of a given term or a type error.
     */
    String typeNameOf(FJExpr expression) throws TypeError {
        if (expression instanceof final FJVariable variable) { // T-Var
            final String varName = variable.name;
            if (this.context.containsKey(varName)) {
                return this.context.get(varName);
            } else {
                throw new VariableNotFound(varName);
            }
        } else if (expression instanceof final FJFieldAccess fieldAccess) { // T-Field
            final String typeName = this.typeNameOf(fieldAccess.object);

            final Optional<List<FJField>> fields = FJUtils.classFields(this.classTable, typeName);
            if (fields.isEmpty()) {
                throw new ClassNotFound(typeName);
            }

            // NOTE: `filter` iterates over all elements while we could abort sooner if a value is found.
            // TODO: Find a way to avoid unnecessary filtering.
            final Optional<FJField> field = fields.get().stream()
                .filter(f -> Objects.equals(f.name, fieldAccess.fieldName))
                .findFirst();
            if (field.isPresent()) {
                return field.get().type;
            } else {
                throw new FieldNotFound(fieldAccess.fieldName);
            }
        } else if (expression instanceof final FJMethodInvocation methodInvocation) { // T-Invk
            final String typeName = this.typeNameOf(methodInvocation);
            final List<FJExpr> args = methodInvocation.args;

            final Optional<FJMethodTypeSignature> methodTypeSignature =
                FJUtils.methodType(this.classTable, methodInvocation.methodName, typeName);
            if (methodTypeSignature.isEmpty()) {
                throw new MethodNotFound(methodInvocation.methodName, typeName);
            }
            final List<String> parameterTypes = methodTypeSignature.get().parameterTypeNames;

            if (args.size() != parameterTypes.size()) {
                throw new ParamsTypeMismatch(new ArrayList<>());
            }
            var temp = new ArrayList<TypeMismatch>();
            for (int i = 0; i < args.size(); i++) {
                final FJExpr arg = args.get(i);
                final String type = parameterTypes.get(i);
                temp.add(new TypeMismatch(FJUtils.lambdaMark(arg, type), type));
            }

            // Check method invocation arguments typing
            for (TypeMismatch tm: temp) {
                final String type;
                try {
                    type = this.typeNameOf(tm.expression);
                } catch (TypeError e) {
                    throw new ParamsTypeMismatch(temp);
                }
                if (!FJUtils.isSubtype(this.classTable, type, tm.expectedTypeName)) {
                    throw new ParamsTypeMismatch(temp);
                }
            }

            // Method invocation is correctly typed
            return methodTypeSignature.get().returnTypeName;
        } else if (expression instanceof final FJCreateObject createObject) { // T-New
            final String typeName = createObject.className;
            final List<FJExpr> args = createObject.args;

            final Optional<List<FJField>> fields = FJUtils.classFields(this.classTable, typeName);
            if (fields.isEmpty()) {
                throw new ClassNotFound(typeName);
            }
            if (args.size() != fields.get().size()) {
                throw new ParamsTypeMismatch(new ArrayList<>());
            }
            var temp = new ArrayList<TypeMismatch>();
            for (int i = 0; i < args.size(); i++) {
                final FJExpr arg = args.get(i);
                final FJField field = fields.get().get(i);
                temp.add(new TypeMismatch(FJUtils.lambdaMark(arg, field.type), field.type));
            }

            // Check object creation arguments typing
            for (TypeMismatch tm: temp) {
                final String type;
                try {
                    type = this.typeNameOf(tm.expression);
                } catch (TypeError e) {
                    throw new ParamsTypeMismatch(temp);
                }
                if (!FJUtils.isSubtype(this.classTable, type, tm.expectedTypeName)) {
                    throw new ParamsTypeMismatch(temp);
                }
            }

            // Object creation is correctly typed
            return createObject.className;
        } else if (expression instanceof final FJCast cast) {
            if (cast.body instanceof final FJLambda lambda) { // T-Lam
                final String lambdaReturnType = cast.typeName;

                HashMap<String, String> lambdaContext = this.context;
                lambda.args.forEach(arg -> lambdaContext.putIfAbsent(arg.name, arg.type));

                final Optional<List<FJSignature>> abstractMethods =
                    FJUtils.abstractMethods(this.classTable, lambdaReturnType);
                if (abstractMethods.isEmpty() || abstractMethods.get().size() != 1) {
                    throw new WrongLambdaType(lambdaReturnType, lambda);
                }
                FJSignature method = abstractMethods.get().get(0);

                final String expectedTypeName = new FJTypeChecker(this.classTable, lambdaContext)
                    .typeNameOf(FJUtils.lambdaMark(lambda, method.returnTypeName));
                if (FJUtils.isSubtype(classTable, expectedTypeName, method.returnTypeName)
                    && method.args.get(0).equals(lambda.args.get(0))
                ) {
                    return lambdaReturnType;
                } else {
                    throw new WrongLambdaType(lambdaReturnType, lambda);
                }
            } else {
                final String expectedTypeName = this.typeNameOf(FJUtils.lambdaMark(cast.body, cast.typeName));

                final boolean expectedTypeIsType =
                    FJUtils.isSubtype(this.classTable, expectedTypeName, cast.typeName);
                final boolean typeIsExpectedType =
                    FJUtils.isSubtype(this.classTable, cast.typeName, expectedTypeName);

                if ((expectedTypeIsType) // T-UCast
                    || (typeIsExpectedType && !cast.typeName.equals(expectedTypeName)) // T-DCast
                    || (!typeIsExpectedType && !expectedTypeIsType) // T-SCast
                ) {
                    return cast.typeName;
                } else {
                    throw new WrongCast(cast.typeName, cast.body);
                }
            }
        } else if (expression instanceof FJLambda) { // Error: Lambda expression without a type
            throw new WrongLambdaType("None", expression);
        } else {
            throw new RuntimeException("Unexpected code path: expression type not supported.");
        }
    }

    /**
     * Checks if a method is well formed.
     * @return {@code Boolean.TRUE} for a well formed method, {@code Boolean.FALSE} otherwise.
     **/
    public Boolean methodTyping(String className, FJMethod method) {
        HashMap<String, String> methodContext = this.context;
        for (FJField arg: method.signature.args) {
            methodContext.put(arg.name, arg.type);
        }
        methodContext.put("this", className);

        final String expectedReturnTypeName;
        try {
            expectedReturnTypeName = new FJTypeChecker(this.classTable, methodContext)
                .typeNameOf(FJUtils.lambdaMark(method.body, method.signature.returnTypeName));
        } catch (TypeError e) {
            return false; // Error obtaining type of expression
        }

        final Optional<List<FJMethod>> methods = FJUtils.methods(this.classTable, expectedReturnTypeName);
        if (methods.isEmpty()) {
            return false; // Error obtaining methods
        }
        return FJUtils.isSubtype(this.classTable, expectedReturnTypeName, method.signature.returnTypeName)
            && methods.get().contains(method);
    }

    /**
     * Checks if a class is well-formed.
     * @return {@code Boolean.TRUE} for a well-formed class, {@code Boolean.FALSE} otherwise.
     */
    public Boolean classTyping(FJClass fjClass) {
        final FJConstructor constructor = fjClass.constructor;

        // Get superclass fields or return false if not found.
        Optional<List<FJField>> _superFields = FJUtils.classFields(this.classTable, fjClass.extendsName);
        if (_superFields.isEmpty()) {
            return false;
        }
        List<FJField> superFields = _superFields.get();

        // Make sure all fields are passed to the constructor.
        List<FJField> allFields = Stream.concat(superFields.stream(), fjClass.fields.stream())
            .collect(Collectors.toList());
        if (!constructor.args.equals(allFields)) {
            return false;
        }

        // Make sure constructor argument names match field names.
        if (constructor.fieldInits.stream().anyMatch(f -> !f.fieldName.equals(f.argumentName))) {
            return false;
        }

        final Optional<List<FJSignature>> _abstractMethods =
            FJUtils.abstractMethods(this.classTable, fjClass.name);
        if (_abstractMethods.isEmpty()) {
            return false; // Error obtaining abstract methods
        }
        final List<FJSignature> abstractMethods = _abstractMethods.get();

        // Make sure all constructor arguments are used
        final List<String> args = constructor.args.stream().map(a -> a.name)
            .collect(Collectors.toList());
        final List<String> usedArgs = Stream.concat(
            constructor.superArgs.stream(),
            constructor.fieldInits.stream().map(fi -> fi.fieldName)
        ).collect(Collectors.toList());

        return abstractMethods.isEmpty()
            && (args.equals(usedArgs))
            && fjClass.methods.stream().allMatch(m -> this.methodTyping(fjClass.name, m));
    }

    /**
     * Checks if an interface is well-formed.
     * @return {@code Boolean.TRUE} for a well-formed interface, {@code Boolean.FALSE} otherwise.
     */
    public Boolean interfaceTyping(FJInterface fjInterface) {
        throw new RuntimeException("Not implemented yet.");
    }
}
