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

public record FJCreateObject(
    String className,
    List<FJExpr> args
) implements FJExpr {
    @Override
    public String getTypeName(
        final HashMap<String, FJType> classTable,
        final HashMap<String, String> context
    ) throws TypeError { // T-New
        final Optional<List<FJField>> fields = FJUtils.classFields(classTable, this.className);
        if (fields.isEmpty()) throw new ClassNotFound(this.className);
        if (this.args.size() != fields.get().size()) throw new ParamsTypeMismatch(new ArrayList<>());

        var temp = new ArrayList<TypeMismatch>();
        for (int i = 0; i < this.args.size(); i++) {
            final FJExpr arg = this.args.get(i);
            final FJField field = fields.get().get(i);
            temp.add(new TypeMismatch(arg.lambdaMark(field.type()), field.type()));
        }

        // Check object creation arguments typing
        for (final TypeMismatch tm : temp) {
            final String type;
            try {
                type = tm.expression().getTypeName(classTable, context);
            } catch (TypeError e) {
                throw new ParamsTypeMismatch(temp);
            }
            if (!FJUtils.isSubtype(classTable, type, tm.expectedTypeName())) {
                throw new ParamsTypeMismatch(temp);
            }
        }

        // Object creation is correctly typed
        return this.className;
    }

    @Override
    public FJCreateObject removingRuntimeAnnotation() {
        return new FJCreateObject(this.className, this.args.stream().map(FJExpr::removingRuntimeAnnotation).toList());
    }

    @Override
    public Boolean isValue() {
        // NOTE: `allMatch` returns `true` if `args.isEmpty()`.
        return args.stream().allMatch(FJExpr::isValue);
    }

    @Override
    public Optional<FJExpr> _eval(final HashMap<String, FJType> classTable) { // RC-New-Arg
        final List<FJExpr> args = this.args().stream().map(e -> e.eval(classTable)).toList();
        return Optional.of(new FJCreateObject(this.className(), args));
    }

    @Override
    public Optional<FJExpr> substitute(final List<String> parameterNames, final List<FJExpr> args) {
        final List<FJExpr> _args = this.args().stream()
            .map(a -> a.substitute(parameterNames, args))
            .flatMap(Optional::stream).toList();
        return Optional.of(new FJCreateObject(this.className(), _args));
    }
}
