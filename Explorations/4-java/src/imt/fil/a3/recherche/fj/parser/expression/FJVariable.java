package imt.fil.a3.recherche.fj.parser.expression;

import imt.fil.a3.recherche.fj.parser.error.TypeError;
import imt.fil.a3.recherche.fj.parser.error.VariableNotFound;
import imt.fil.a3.recherche.fj.parser.type.FJType;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

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
    public FJVariable removingRuntimeAnnotation() { return this; }

    @Override
    public Boolean isValue() { return false; }

    @Override
    public Optional<FJExpr> _eval(final HashMap<String, FJType> classTable) { return Optional.empty(); }

    @Override
    public Optional<FJExpr> substitute(final List<String> parameterNames, final List<FJExpr> args) {
        final int index = parameterNames.indexOf(this.name);
        if (index >= 0 && args.size() > index) {
            return Optional.of(args.get(index));
        } else {
            return Optional.empty();
        }
    }
}
