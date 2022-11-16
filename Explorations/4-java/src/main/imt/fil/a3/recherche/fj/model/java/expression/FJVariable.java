package imt.fil.a3.recherche.fj.model.java.expression;

import imt.fil.a3.recherche.fj.model.TypeCheckingContext;
import imt.fil.a3.recherche.fj.model.TypeTable;
import imt.fil.a3.recherche.fj.model.error.TypeError;
import imt.fil.a3.recherche.fj.model.error.VariableNotFound;

import java.util.List;
import java.util.Optional;

public record FJVariable(String name) implements FJExpr {
    @Override
    public String getTypeName(final TypeCheckingContext context) throws TypeError { // T-Var
        final Optional<String> typeName = context.typeName(this.name);
        if (typeName.isPresent()) {
            return typeName.get();
        } else {
            throw new VariableNotFound(this.name);
        }
    }

    @Override
    public FJVariable removingRuntimeAnnotation() { return this; }

    @Override
    public Boolean isValue() { return false; }

    @Override
    public Optional<FJExpr> _eval(final TypeTable typeTable) { return Optional.empty(); }

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
