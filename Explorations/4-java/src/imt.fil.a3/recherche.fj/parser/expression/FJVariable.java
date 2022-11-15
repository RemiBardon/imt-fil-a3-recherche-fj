package imt.fil.a3.recherche.fj.parser.expression;

import imt.fil.a3.recherche.fj.parser.error.TypeError;
import imt.fil.a3.recherche.fj.parser.error.VariableNotFound;
import imt.fil.a3.recherche.fj.parser.type.FJType;

import java.util.HashMap;

public record FJVariable(String name) implements FJExpr {
    @Override
    public String getTypeName(
        final HashMap<String, FJType> classTable,
        final HashMap<String, String> context
    ) throws TypeError { // T-Var
        if (context.containsKey(this.name)) {
            return context.get(this.name);
        } else {
            throw new VariableNotFound(this.name);
        }
    }

    @Override
    public Boolean isValue() { return false; }

    @Override
    public FJVariable removingRuntimeAnnotation() { return this; }
}
