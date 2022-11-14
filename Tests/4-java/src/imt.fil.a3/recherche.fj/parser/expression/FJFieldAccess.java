package imt.fil.a3.recherche.fj.parser.expression;

import imt.fil.a3.recherche.fj.FJUtils;
import imt.fil.a3.recherche.fj.parser.FJField;
import imt.fil.a3.recherche.fj.parser.error.ClassNotFound;
import imt.fil.a3.recherche.fj.parser.error.FieldNotFound;
import imt.fil.a3.recherche.fj.parser.error.TypeError;
import imt.fil.a3.recherche.fj.parser.error.VariableNotFound;
import imt.fil.a3.recherche.fj.parser.type.FJType;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class FJFieldAccess implements FJExpr {
    public final FJExpr object;
    public final String fieldName;

    public FJFieldAccess(FJExpr object, String fieldName) {
        this.object = object;
        this.fieldName = fieldName;
    }

    @Override
    public String getTypeName(
        final HashMap<String, FJType> classTable,
        final HashMap<String, String> context
    ) throws TypeError { // T-Field
        final String typeName = this.object.getTypeName(classTable, context);

        final Optional<List<FJField>> fields = FJUtils.classFields(classTable, typeName);
        if (fields.isEmpty()) throw new ClassNotFound(typeName);

        // NOTE: `filter` iterates over all elements while we could abort sooner if a value is found.
        // TODO: Find a way to avoid unnecessary filtering.
        final Optional<FJField> field = fields.get().stream()
            .filter(f -> f.name.equals(this.fieldName))
            .findFirst();
        if (field.isPresent()) {
            return field.get().type;
        } else {
            throw new FieldNotFound(this.fieldName);
        }
    }
}
