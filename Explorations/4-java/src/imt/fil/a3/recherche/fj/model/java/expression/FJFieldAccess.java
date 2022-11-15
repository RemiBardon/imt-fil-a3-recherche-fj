package imt.fil.a3.recherche.fj.model.java.expression;

import imt.fil.a3.recherche.fj.model.TypeTable;
import imt.fil.a3.recherche.fj.model.error.ClassNotFound;
import imt.fil.a3.recherche.fj.model.error.FieldNotFound;
import imt.fil.a3.recherche.fj.model.error.TypeError;
import imt.fil.a3.recherche.fj.model.java.misc.FJField;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public record FJFieldAccess(FJExpr object, String fieldName) implements FJExpr {
    @Override
    public String getTypeName(
        final TypeTable typeTable,
        final HashMap<String, String> context
    ) throws TypeError { // T-Field
        final String typeName = this.object.getTypeName(typeTable, context);

        final Optional<List<FJField>> fields = typeTable.classFields(typeName);
        if (fields.isEmpty()) throw new ClassNotFound(typeName);

        // NOTE: `filter` iterates over all elements while we could abort sooner if a value is found.
        // TODO: Find a way to avoid unnecessary filtering.
        final Optional<FJField> field = fields.get().stream()
            .filter(f -> f.name().equals(this.fieldName))
            .findFirst();
        if (field.isPresent()) {
            return field.get().type();
        } else {
            throw new FieldNotFound(this.fieldName);
        }
    }

    @Override
    public FJFieldAccess removingRuntimeAnnotation() {
        return new FJFieldAccess(this.object.removingRuntimeAnnotation(), this.fieldName);
    }

    @Override
    public Boolean isValue() { return false; }

    @Override
    public Optional<FJExpr> _eval(final TypeTable typeTable) {
        if (this.object.isValue()) { // R-Field
            if (this.object instanceof final FJCreateObject createObject) {
                final Optional<List<FJField>> _fields = typeTable.classFields(createObject.className());
                if (_fields.isEmpty()) return Optional.empty();
                final List<FJField> fields = _fields.get();

                Optional<Integer> index = Optional.empty();
                for (int i = 0; i < fields.size(); i++) {
                    if (fields.get(i).name().equals(this.fieldName)) {
                        index = Optional.of(i);
                        break;
                    }
                }
                if (index.isEmpty()) return Optional.empty();

                final FJExpr arg = createObject.args().get(index.get());
                final String lambdaTypeName = fields.get(index.get()).name();
                return Optional.of(arg.lambdaMark(lambdaTypeName));
            } else {
                return Optional.empty(); // Not an object instance
            }
        } else { // RC-Field
            return this.object._eval(typeTable).map(e -> new FJFieldAccess(e, this.fieldName));
        }
    }

    @Override
    public Optional<FJExpr> substitute(final List<String> parameterNames, final List<FJExpr> args) {
        return this.object.substitute(parameterNames, args)
            .map(e -> new FJFieldAccess(e, this.fieldName));
    }
}
