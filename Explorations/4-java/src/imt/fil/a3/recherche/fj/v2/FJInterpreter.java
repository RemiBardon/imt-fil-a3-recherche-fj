package imt.fil.a3.recherche.fj.v2;

import imt.fil.a3.recherche.fj.parser.expression.*;
import imt.fil.a3.recherche.fj.parser.type.FJType;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public record FJInterpreter(HashMap<String, FJType> classTable) {
    /**
     * Replaces actual parameters in method body expression.
     *
     * @return A new changed expression.
     */
    public static Optional<FJExpr> substitute(List<String> parameterNames, List<FJExpr> args, FJExpr body) {
        if (body instanceof final FJVariable fjVariable) {
            final int index = parameterNames.indexOf(fjVariable.name());
            if (index >= 0 && args.size() > index) {
                return Optional.of(args.get(index));
            } else {
                return Optional.empty();
            }
        } else if (body instanceof final FJFieldAccess fieldAccess) {
            return FJInterpreter.substitute(parameterNames, args, fieldAccess.object())
                .map(e -> new FJFieldAccess(e, fieldAccess.fieldName()));
        } else if (body instanceof final FJMethodInvocation methodInvocation) {
            return FJInterpreter.substitute(parameterNames, args, methodInvocation.source())
                .map(e -> {
                    final List<FJExpr> _args = methodInvocation.args().stream()
                        .map(a -> FJInterpreter.substitute(parameterNames, args, a))
                        .flatMap(Optional::stream).toList();
                    return new FJMethodInvocation(e, methodInvocation.methodName(), _args);
                });
        } else if (body instanceof final FJCreateObject createObject) {
            final List<FJExpr> _args = createObject.args().stream()
                .map(a -> FJInterpreter.substitute(parameterNames, args, a))
                .flatMap(Optional::stream).toList();
            return Optional.of(new FJCreateObject(createObject.className(), _args));
        } else if (body instanceof final FJCast fjCast) {
            return FJInterpreter.substitute(parameterNames, args, fjCast.body())
                .map(e -> new FJCast(fjCast.typeName(), e));
        } else if (body instanceof FJLambda) {
            return Optional.of(body); // Do nothing
        } else {
            throw new RuntimeException("Unexpected code path: expression type not supported.");
        }
    }

}
