package imt.fil.a3.recherche.fj;

import imt.fil.a3.recherche.fj.parser.*;
import imt.fil.a3.recherche.fj.parser.expression.FJExpr;
import imt.fil.a3.recherche.fj.parser.type.FJType;

import java.util.HashMap;
import java.util.List;


public class FJUtils {

    public static boolean isSubtype(HashMap<String, FJType> classTable, String type1, String type2) {
        return false;
    }

    public static List<FJField> classFields(HashMap<String, FJType> classTable, String className) {
        return null;
    }

    public static List<FJSignature> abstractMethods(HashMap<String, FJType> classTable, String className) {
        return null;
    }

    public static List<FJMethod> methods(HashMap<String, FJType> classTable, String className) {
        return null;
    }

    public static Pair<List<String>, String> methodType(HashMap<String, FJType> classTable, String methodName, String className) {
        return null;
    }

    public static Pair<List<String>, FJExpr> methodBody(HashMap<String, FJType> classTable, String methodName, String className) {
        return null;
    }

    public static boolean isValue(HashMap<String, FJType> classTable, FJExpr expr) {
        return false;
    }

    public static FJExpr lambdaMark(HashMap<String, FJType> classTable, FJExpr expr) {
        return null;
    }

    public static FJExpr removeRuntimeAnnotation(FJExpr expr) {
        return null;
    }
}
