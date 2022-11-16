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

    public Optional<String> typeName(String variableName) {
        return Optional.ofNullable(context.get(variableName));
    }

    public TypeCheckingContext with(List<FJField> fields) {
        final TypeCheckingContext res = this.copy();
        fields.forEach(arg -> res.context.put(arg.name(), arg.type()));
        return res;
    }

    public TypeCheckingContext copy() {
        return new TypeCheckingContext(this.typeTable.copy(), new HashMap<>(this.context));
    }

    public TypeCheckingContext with(String variableName, String variableType) {
        final TypeCheckingContext res = this.copy();
        res.context.put(variableName, variableType);
        return res;
    }
}
