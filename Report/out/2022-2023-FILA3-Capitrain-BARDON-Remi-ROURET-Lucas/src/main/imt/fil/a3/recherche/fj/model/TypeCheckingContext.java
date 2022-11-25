package imt.fil.a3.recherche.fj.model;

import imt.fil.a3.recherche.fj.model.java.misc.FJField;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

@SuppressWarnings("ClassCanBeRecord")
public final class TypeCheckingContext {
    public final static Logger logger = Logger.getLogger("imt.fil.a3.recherche.fj.type-checker");

    public final TypeTable typeTable;
    private final Map<String, String> context;

    public TypeCheckingContext(final TypeTable typeTable, final Map<String, String> context) {
        this.typeTable = typeTable;
        this.context = context;
    }

    /**
     * Gets the type of given variable.
     *
     * @param variableName Name of the variable.
     * @return Name of the type of the variable.
     */
    public Optional<String> typeName(final String variableName) {
        return Optional.ofNullable(context.get(variableName));
    }

    /**
     * Adds a list of variables to the context.
     *
     * @param fields Fields to add to the new context.
     * @return A new context with the added variables.
     */
    public TypeCheckingContext with(final List<FJField> fields) {
        final TypeCheckingContext res = this.copy();
        res.add(fields);
        return res;
    }

    /**
     * Copy the context.
     *
     * @return A new context with the same variables.
     */
    public TypeCheckingContext copy() {
        return new TypeCheckingContext(this.typeTable.copy(), new HashMap<>(this.context));
    }

    /**
     * Adds a list of variables to the context.
     *
     * @param fields Fields to add to {@code this}.
     */
    public void add(final List<FJField> fields) {
        fields.forEach(arg -> this.context.put(arg.name(), arg.type()));
    }

    /**
     * Adds a variable to the context.
     *
     * @param variableName Name of the variable to add to the new context.
     * @param variableType Name of the type of the variable to add to the new context.
     * @return A new context with the added variable.
     */
    public TypeCheckingContext with(final String variableName, final String variableType) {
        final TypeCheckingContext res = this.copy();
        res.add(variableName, variableType);
        return res;
    }

    /**
     * Adds a variable to the context.
     *
     * @param variableName Name of the variable to add to {@code this}.
     * @param variableType Name of the type of the variable to add to {@code this}.
     */
    public void add(final String variableName, final String variableType) {
        this.context.put(variableName, variableType);
    }
}
