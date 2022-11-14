package imt.fil.a3.recherche.fj.parser.expression;

import imt.fil.a3.recherche.fj.parser.error.TypeError;
import imt.fil.a3.recherche.fj.parser.type.FJType;

import java.util.HashMap;

/**
 * Expression.
 */
public interface FJExpr {
    /**
     * Checks the type of a given expression.
     * @return The type of a given term or a type error.
     */
    String getTypeName(
        final HashMap<String, FJType> classTable,
        final HashMap<String, String> context
    ) throws TypeError;
}
