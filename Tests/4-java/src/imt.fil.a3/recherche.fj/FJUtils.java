package imt.fil.a3.recherche.fj;

import imt.fil.a3.recherche.fj.parser.*;
import imt.fil.a3.recherche.fj.parser.expression.FJExpr;
import imt.fil.a3.recherche.fj.parser.type.FJClass;
import imt.fil.a3.recherche.fj.parser.type.FJInterface;
import imt.fil.a3.recherche.fj.parser.type.FJType;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;


public class FJUtils {

    public static Boolean isSubtype(HashMap<String, FJType> classTable, String classA, String classB) {
        if(classA.equals(classB)) return true;

        FJType fjType = classTable.get(classA);

        if(fjType instanceof FJClass){
            FJClass fjClass = (FJClass) fjType;
            if( fjClass.extendsName.equals(classB) || fjClass.implementsNames.contains(classB)){
                return true;
            }else{
                return isSubtype(classTable,fjClass.extendsName,classB) ||
                        fjClass.implementsNames.stream()
                        .filter(implementName -> isSubtype(classTable,implementName,classB))
                        .findFirst()
                        .isPresent();
            }
        } else if (fjType instanceof FJInterface) {
            FJInterface fjInterface = (FJInterface) fjType;
            return fjInterface.extendsNames.contains(classB) ||
                    fjInterface.extendsNames.stream()
                            .filter(implementName -> isSubtype(classTable,implementName,classB))
                            .findFirst()
                            .isPresent();
        }
        else{
            return false;
        }
    }

    public static Optional<List<FJField>> classFields(
        HashMap<String, FJType> classTable,
        String className
    ) {
        throw new RuntimeException("Not implemented yet.");
    }

    public static Optional<List<FJSignature>> abstractMethods(
        HashMap<String, FJType> classTable,
        String className
    ) {
        throw new RuntimeException("Not implemented yet.");
    }

    public static Optional<List<FJMethod>> methods(
        HashMap<String, FJType> classTable,
        String className
    ) {
        throw new RuntimeException("Not implemented yet.");
    }

    public static Optional<FJMethodTypeSignature> methodType(
        HashMap<String, FJType> classTable,
        String methodName,
        String className
    ) {
        throw new RuntimeException("Not implemented yet.");
    }

    public static Optional<FJMethodBodySignature> methodBody(
        HashMap<String, FJType> classTable,
        String methodName,
        String className
    ) {
        throw new RuntimeException("Not implemented yet.");
    }

    public static Boolean isValue(HashMap<String, FJType> classTable, FJExpr expr) {
        throw new RuntimeException("Not implemented yet.");
    }

    public static FJExpr lambdaMark(FJExpr expr, String type) {
        throw new RuntimeException("Not implemented yet.");
    }

    public static FJExpr removeRuntimeAnnotation(FJExpr expr) {
        throw new RuntimeException("Not implemented yet.");
    }
}
