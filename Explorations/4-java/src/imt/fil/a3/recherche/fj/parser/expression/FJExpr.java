package imt.fil.a3.recherche.fj.parser.expression;

import imt.fil.a3.recherche.fj.parser.error.TypeError;
import imt.fil.a3.recherche.fj.parser.type.FJType;

import java.util.HashMap;
import java.util.Optional;

/**
 * Expression.
 */
public interface FJExpr {
    /**
     * Checks the type of a given expression.
     *
     * @return The type of a given term or a type error.
     */
    String getTypeName(
        final HashMap<String, FJType> classTable,
        final HashMap<String, String> context
    ) throws TypeError;

    /**
     * Annotates the types for lambda expressions.
     *
     * @return A lambda expression annotated with its type, or the expression if
     * it is not a lambda expression.
     */
    default FJExpr lambdaMark(final String typeName) { return this; }

    /**
     * Removes runtime annotations from lambda expressions.
     *
     * @return An expression without the runtime type annotations.
     */
    FJExpr removingRuntimeAnnotation();

    /**
     * Evaluates an expression recursively.
     *
     * @return A value after all the reduction steps.
     */
    default FJExpr eval(final HashMap<String, FJType> classTable) {
        return this.isValue() ? this : this._eval(classTable).orElse(this);
    }

    /**
     * Checks if an expression represents a value.
     *
     * @return Boolean indicating if an expression is a value.
     */
    Boolean isValue();

    /**
     * Evaluates an expression.
     *
     * @return An expression after processing one reduction step.
     */
    Optional<FJExpr> _eval(final HashMap<String, FJType> classTable);

}
