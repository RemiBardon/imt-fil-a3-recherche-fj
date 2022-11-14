package imt.fil.a3.recherche.fj.parser.expression;

import imt.fil.a3.recherche.fj.FJUtils;
import imt.fil.a3.recherche.fj.parser.FJMethodTypeSignature;
import imt.fil.a3.recherche.fj.parser.TypeMismatch;
import imt.fil.a3.recherche.fj.parser.error.MethodNotFound;
import imt.fil.a3.recherche.fj.parser.error.ParamsTypeMismatch;
import imt.fil.a3.recherche.fj.parser.error.TypeError;
import imt.fil.a3.recherche.fj.parser.type.FJType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public final class FJMethodInvocation implements FJExpr {
    public final FJExpr source;
    public final String methodName;
    public final List<FJExpr> args;

    public FJMethodInvocation(
        final FJExpr source,
        final String methodName,
        final List<FJExpr> args
    ) {
        this.source = source;
        this.methodName = methodName;
        this.args = args;
    }

    @Override
    public String getTypeName(
        final HashMap<String, FJType> classTable,
        final HashMap<String, String> context
    ) throws TypeError { // T-Invk
        final String typeName = this.source.getTypeName(classTable, context);

        final Optional<FJMethodTypeSignature> methodTypeSignature =
                FJUtils.methodType(classTable, this.methodName, typeName);
        if (methodTypeSignature.isEmpty()) {
            throw new MethodNotFound(this.methodName, typeName);
        }
        final List<String> parameterTypes = methodTypeSignature.get().parameterTypeNames;

        if (this.args.size() != parameterTypes.size()) {
            throw new ParamsTypeMismatch(new ArrayList<>());
        }

        var temp = new ArrayList<TypeMismatch>();
        for (int i = 0; i < this.args.size(); i++) {
            final FJExpr arg = this.args.get(i);
            final String type = parameterTypes.get(i);
            temp.add(new TypeMismatch(FJUtils.lambdaMark(arg, type), type));
        }

        // Check method invocation arguments typing
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

        // Method invocation is correctly typed
        return methodTypeSignature.get().returnTypeName;
    }
}
