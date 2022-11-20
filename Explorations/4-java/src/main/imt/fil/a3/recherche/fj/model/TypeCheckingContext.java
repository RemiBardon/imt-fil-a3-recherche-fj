package imt.fil.a3.recherche.fj.model;

import imt.fil.a3.recherche.fj.model.java.misc.FJField;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

public final class TypeCheckingContext {
    public final static Logger logger = Logger.getLogger("imt.fil.a3.recherche.fj.type-checker");

    public final TypeTable typeTable;
    private final Map<String, String> context;

    public TypeCheckingContext(final TypeTable typeTable, final Map<String, String> context) {
        this.typeTable = typeTable;
        this.context = context;
    }

    /**
     * * Gets the type of given variable.
     * @param variableName
     * @return
     */
    public Optional<String> typeName(String variableName) {
        return Optional.ofNullable(context.get(variableName));
    }

    /**
     * * Adds a list of variables to the context.
     * @param fields
     * @return A new context with the added variables.
     */
    public TypeCheckingContext with(List<FJField> fields) {
        final TypeCheckingContext res = this.copy();
        fields.forEach(arg -> res.context.put(arg.name(), arg.type()));
        return res;
    }

    /**
     * Copy the context.
     * @return A new context with the same variables.
     */
    public TypeCheckingContext copy() {
        return new TypeCheckingContext(this.typeTable.copy(), new HashMap<>(this.context));
    }

    /**
     * Adds a variable to the context.
     * @param variableName
     * @param variableType
     * @return A new context with the added variable.
     */
    public TypeCheckingContext with(String variableName, String variableType) {
        final TypeCheckingContext res = this.copy();
        res.context.put(variableName, variableType);
        return res;
    }
}
