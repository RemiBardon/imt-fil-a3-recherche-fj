package imt.fil.a3.recherche.fj.parser.expression;

import imt.fil.a3.recherche.fj.FJUtils;
import imt.fil.a3.recherche.fj.parser.FJField;
import imt.fil.a3.recherche.fj.parser.FJSignature;
import imt.fil.a3.recherche.fj.parser.error.TypeError;
import imt.fil.a3.recherche.fj.parser.error.WrongLambdaType;
import imt.fil.a3.recherche.fj.parser.type.FJType;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public record FJLambda(List<FJField> args, FJExpr body) implements FJExpr {
    @Override
    public String getTypeName(
        final HashMap<String, FJType> classTable,
        final HashMap<String, String> context
    ) throws TypeError {
        // Error: Lambda expression without a type
        throw new WrongLambdaType("None", this);
    }

    @Override
    public FJExpr lambdaMark(final String typeName) {
        return new FJCast(typeName, this);
    }

    @Override
    public FJLambda removingRuntimeAnnotation() {
        return new FJLambda(this.args, this.body.removingRuntimeAnnotation());
    }

    @Override
    public Boolean isValue() { return true; }

    @Override
    public Optional<FJExpr> _eval(HashMap<String, FJType> classTable) { return Optional.of(this); }

    @Override
    public Optional<FJExpr> substitute(List<String> parameterNames, List<FJExpr> args) {
        return Optional.of(this); // Do nothing
    }

    public String getTypeName(
        final HashMap<String, FJType> classTable,
        final HashMap<String, String> context,
        final String returnType
    ) throws TypeError {
        HashMap<String, String> lambdaContext = new HashMap<>(context);
        this.args.forEach(arg -> lambdaContext.putIfAbsent(arg.name(), arg.type()));

        final Optional<List<FJSignature>> abstractMethods =
            FJUtils.abstractMethods(classTable, returnType);
        // Lambdas have only one abstract method: themselves
        if (abstractMethods.isEmpty() || abstractMethods.get().size() != 1) {
            throw new WrongLambdaType(returnType, this);
        }
        final FJSignature method = abstractMethods.get().get(0);

        final String expectedTypeName = this.lambdaMark(method.returnTypeName())
            .getTypeName(classTable, lambdaContext);
        final boolean returnTypeIsCorrect = FJUtils.isSubtype(classTable, expectedTypeName, method.returnTypeName());
        final boolean argsTypesAreCorrect = method.args().get(0).equals(this.args.get(0));

        if (returnTypeIsCorrect && argsTypesAreCorrect) {
            return returnType;
        } else {
            throw new WrongLambdaType(returnType, this);
        }
    }
}
