package imt.fil.a3.recherche.fj;

import java.util.List;

class FJClass {
    private String name;
    private String extendsName;
    private List<String> implementsNames;

    private List<FJField> fields;
    private List<FJMethod> methods;
    private FJConstructor constructor;

    public FJClass(
            String name,
            String extendsName,
            List<String> implementsNames,
            List<FJField> fields,
            List<FJMethod> methods,
            FJConstructor constructor
    ) {
        this.name = name;
        this.extendsName = extendsName;
        this.implementsNames = implementsNames;
        this.fields = fields;
        this.methods = methods;
        this.constructor = constructor;
    }
}

class FJInterface{
    private String name;
    private List<String> extendsNames;

    private List<FJMethod> methods;
    private List<FJSignature> signatures;
    private List<FJMethod> defaultMethods;

    public FJInterface(
            String name,
            List<String> extendsNames,
            List<FJMethod> methods,
            List<FJSignature> signatures,
            List<FJMethod> defaultMethods
    ) {
        this.name = name;
        this.extendsNames = extendsNames;
        this.methods = methods;
        this.signatures = signatures;
        this.defaultMethods = defaultMethods;
    }
}


class FJConstructor{
    private String name;
    private List<FJField> args;
    private List<String> superArgs;
    private List<FieldInit> fieldInits;

    public FJConstructor(
            String name,
            List<FJField> args,
            List<String> superArgs,
            List<FieldInit> fieldInits
    ) {
        this.name = name;
        this.args = args;
        this.superArgs = superArgs;
        this.fieldInits = fieldInits;
    }
}

class FJSignature{
    private String typeName;
    private String name;
    private List<FJField> args;

    public FJSignature(
            String typeName,
            String name,
            List<FJField> args
    ) {
        this.typeName = typeName;
        this.name = name;
        this.args = args;
    }
}

class FJMethod{
    private FJSignature signature;
    private FJExpr body;

    public FJMethod(FJSignature signature, FJExpr body) {
        this.signature = signature;
        this.body = body;
    }
}

class FJField{
    private String type;
    private String name;

    public FJField(String type, String name) {
        this.type = type;
        this.name = name;
    }
}

class FieldInit{
    private String fieldName;
    private String argumentName;

    public FieldInit(String fieldName, String argumentName) {
        this.fieldName = fieldName;
        this.argumentName = argumentName;
    }
}

class TypeMismatch{
    private FJExpr expression;
    private String expectedTypeName;

    public TypeMismatch(FJExpr expression, String expectedTypeName) {
        this.expression = expression;
        this.expectedTypeName = expectedTypeName;
    }
}

// Expression
interface FJExpr{}

final class FJVariable implements FJExpr {
    String name;

    public FJVariable(String name) {
        this.name = name;
    }
}

final class FJFieldAccess implements FJExpr {
    FJExpr object;
    String fieldName;

    public FJFieldAccess(FJExpr object, String fieldName) {
        this.object = object;
        this.fieldName = fieldName;
    }
}

final class FJMethodInvocation implements FJExpr {
    FJExpr object;
    String methodName;
    List<FJExpr> arguments;

    public FJMethodInvocation(FJExpr object, String methodName, List<FJExpr> arguments) {
        this.object = object;
        this.methodName = methodName;
        this.arguments = arguments;
    }
}

final class FJCreateObject implements FJExpr {
    String typeName;
    List<FJExpr> args;

    public FJCreateObject(String typeName, List<FJExpr> args) {
        this.typeName = typeName;
        this.args = args;
    }
}

final class FJCast implements FJExpr {
    String typeName;
    FJExpr body;

    public FJCast(String typeName, FJExpr body) {
        this.typeName = typeName;
        this.body = body;
    }
}

final class FJLambda implements FJExpr {
    List<FJField> args;
    FJExpr body;

    public FJLambda(List<FJField> args, FJExpr body) {
        this.args = args;
        this.body = body;
    }
}

// Type Error
interface TypeError{}

class VariableNotFound implements TypeError {
    String name;

    public VariableNotFound(String name) {
        this.name = name;
    }
}

class FieldNotFound implements TypeError {
    String name;

    public FieldNotFound(String name) {
        this.name = name;
    }
}

class ClassNotFound implements TypeError {
    String name;

    public ClassNotFound(String name) {
        this.name = name;
    }
}


class MethodNotFound implements TypeError {
    String message;
    String returnTypeName;

    public MethodNotFound(String message, String returnTypeName) {
        this.message = message;
        this.returnTypeName = returnTypeName;
    }
}

class ParamsTypeMismatch implements TypeError {
    List<TypeMismatch> params;

    public ParamsTypeMismatch(List<TypeMismatch> params) {
        this.params = params;
    }
}

class WrongLambdaType implements TypeError {
    String targetTypeName;
    FJExpr lambda;

    public WrongLambdaType(String targetTypeName, FJExpr lambda) {
        this.targetTypeName = targetTypeName;
        this.lambda = lambda;
    }
}

class WrongCast implements TypeError {
    String message;
    FJExpr expression;

    public WrongCast(String message, FJExpr expression) {
        this.message = message;
        this.expression = expression;
    }
}

