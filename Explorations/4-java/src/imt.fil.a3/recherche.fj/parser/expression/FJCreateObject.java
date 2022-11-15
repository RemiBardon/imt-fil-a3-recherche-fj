package imt.fil.a3.recherche.fj.parser.expression;

import imt.fil.a3.recherche.fj.FJUtils;
import imt.fil.a3.recherche.fj.parser.FJField;
import imt.fil.a3.recherche.fj.parser.TypeMismatch;
import imt.fil.a3.recherche.fj.parser.error.ClassNotFound;
import imt.fil.a3.recherche.fj.parser.error.ParamsTypeMismatch;
import imt.fil.a3.recherche.fj.parser.error.TypeError;
import imt.fil.a3.recherche.fj.parser.type.FJType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public final class FJCreateObject implements FJExpr {
    public final String className;
    public final List<FJExpr> args;

    public FJCreateObject(
        final String className,
        final List<FJExpr> args
    ) {
        this.className = className;
        this.args = args;
    }

    @Override
    public String getTypeName(
        final HashMap<String, FJType> classTable,
        final HashMap<String, String> context
    ) throws TypeError { // T-New
        final Optional<List<FJField>> fields = FJUtils.classFields(classTable, this.className);
        if (fields.isEmpty()) throw new ClassNotFound(this.className);
        if (this.args.size() != fields.get().size()) {
            throw new ParamsTypeMismatch(new ArrayList<>());
        }

        var temp = new ArrayList<TypeMismatch>();
        for (int i = 0; i < this.args.size(); i++) {
            final FJExpr arg = this.args.get(i);
            final FJField field = fields.get().get(i);
            temp.add(new TypeMismatch(arg.lambdaMark(field.type), field.type));
        }

        // Check object creation arguments typing
        for (final TypeMismatch tm: temp) {
            final String type;
            try {
                type = tm.expression.getTypeName(classTable, context);
            } catch (TypeError e) {
                throw new ParamsTypeMismatch(temp);
            }
            if (!FJUtils.isSubtype(classTable, type, tm.expectedTypeName)) {
                throw new ParamsTypeMismatch(temp);
            }
        }

        // Object creation is correctly typed
        return this.className;
    }

    @Override
    public Boolean isValue() {
        // NOTE: `allMatch` returns `true` if `args.isEmpty()`.
        return args.stream().allMatch(FJExpr::isValue);
    }

    @Override
    public FJCreateObject removingRuntimeAnnotation() {
        return new FJCreateObject(this.className, this.args.stream().map(FJExpr::removingRuntimeAnnotation).toList());
    }
}
