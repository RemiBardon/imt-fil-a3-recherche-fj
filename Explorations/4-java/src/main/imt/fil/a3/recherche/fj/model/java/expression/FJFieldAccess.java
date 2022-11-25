package imt.fil.a3.recherche.fj.model.java.expression;

import imt.fil.a3.recherche.fj.model.TypeCheckingContext;
import imt.fil.a3.recherche.fj.model.TypeTable;
import imt.fil.a3.recherche.fj.model.error.ClassNotFound;
import imt.fil.a3.recherche.fj.model.error.FieldNotFound;
import imt.fil.a3.recherche.fj.model.error.TypeError;
import imt.fil.a3.recherche.fj.model.java.misc.FJField;
import imt.fil.a3.recherche.fj.model.misc.TypeAnnotatedExpression;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record FJFieldAccess(FJExpr object, String fieldName) implements FJExpr {
    @Override
    public TypeAnnotatedExpression getTypeApproach1(final TypeCheckingContext context) throws TypeError { // T-Field
        // Get class of object on which we want to access the field
        final TypeAnnotatedExpression annotatedExpression = this.object.getTypeApproach1(context);

        // Get fields defined in the class
        final String className = annotatedExpression.typeName();
        final Optional<List<FJField>> fields = context.typeTable.classFields(className);
        if (fields.isEmpty()) throw new ClassNotFound(className);

        // Find field type given its name
        final Optional<FJField> field = fields.get().stream()
            .filter(f -> f.name().equals(this.fieldName))
            .findFirst();

        // Return a type-annotated expression if a field was found
        if (field.isPresent()) {
            return new TypeAnnotatedExpression(
                field.get().type(),
                new FJFieldAccess(annotatedExpression.expression(), this.fieldName)
            );
        } else {
            throw new FieldNotFound(this.fieldName);
        }
    }

    @Override
    public String getTypeNameApproach2(final TypeCheckingContext context) throws TypeError { // T-Field
        final String typeName = this.object.getTypeNameApproach2(context);

        final Optional<List<FJField>> fields = context.typeTable.classFields(typeName);
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
    public Optional<FJExpr> _evalApproach2(final TypeTable typeTable) throws ClassNotFound {
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
            return this.object._evalApproach2(typeTable).map(e -> new FJFieldAccess(e, this.fieldName));
        }
    }

    @Override
    public Optional<FJExpr> substituteApproach2(final List<String> parameterNames, final List<FJExpr> args) {
        return this.object.substituteApproach2(parameterNames, args)
            .map(e -> new FJFieldAccess(e, this.fieldName));
    }
}
