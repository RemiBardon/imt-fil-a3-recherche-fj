package imt.fil.a3.recherche.fj.parser.expression;

import imt.fil.a3.recherche.fj.FJUtils;
import imt.fil.a3.recherche.fj.parser.FJField;
import imt.fil.a3.recherche.fj.parser.FJMethodBodySignature;
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
import java.util.stream.Stream;

public record FJMethodInvocation(
    FJExpr source,
    String methodName,
    List<FJExpr> args
) implements FJExpr {
    @Override
    public String getTypeName(
        final HashMap<String, FJType> classTable,
        final HashMap<String, String> context
    ) throws TypeError { // T-Invk
        final String typeName = this.source.getTypeName(classTable, context);

        final Optional<FJMethodTypeSignature> methodTypeSignature =
            FJUtils.methodType(classTable, this.methodName, typeName);
        if (methodTypeSignature.isEmpty()) throw new MethodNotFound(this.methodName, typeName);
        final List<String> parameterTypes = methodTypeSignature.get().parameterTypeNames();

        if (this.args.size() != parameterTypes.size()) throw new ParamsTypeMismatch(new ArrayList<>());

        var temp = new ArrayList<TypeMismatch>();
        for (int i = 0; i < this.args.size(); i++) {
            final FJExpr arg = this.args.get(i);
            final String type = parameterTypes.get(i);
            temp.add(new TypeMismatch(arg.lambdaMark(type), type));
        }

        // Check method invocation arguments typing
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

        // Method invocation is correctly typed
        return methodTypeSignature.get().returnTypeName();
    }

    @Override
    public FJMethodInvocation removingRuntimeAnnotation() {
        return new FJMethodInvocation(
            this.source.removingRuntimeAnnotation(),
            this.methodName,
            this.args.stream().map(FJExpr::removingRuntimeAnnotation).toList()
        );
    }

    @Override
    public Boolean isValue() { return false; }

    @Override
    public Optional<FJExpr> _eval(final HashMap<String, FJType> classTable) {
        if (this.source().isValue()) {
            if (this.args().stream().allMatch(FJExpr::isValue)) {
                if (this.source() instanceof final FJCreateObject createObject) { // R-Invk
                    final Optional<FJMethodTypeSignature> methodType = FJUtils.methodType(
                        classTable,
                        this.methodName(),
                        createObject.className()
                    );
                    if (methodType.isEmpty()) return Optional.empty(); // No method type

                    final Optional<FJMethodBodySignature> methodBody = FJUtils.methodBody(
                        classTable,
                        this.methodName(),
                        createObject.className()
                    );
                    if (methodBody.isEmpty()) return Optional.empty(); // No method body

                    final List<FJExpr> args = new ArrayList<>();
                    // <=> zip(this.args, methodType.parameterTypeNames)
                    for (int i = 0; i < this.args().size(); i++) {
                        final FJExpr arg = this.args().get(i);
                        final String typeName = methodType.get().parameterTypeNames().get(i);
                        args.add(arg.lambdaMark(typeName));
                    }
                    args.add(this.source());
                    final List<String> parameterNames = Stream.concat(
                        methodBody.get().argumentNames().stream(),
                        Stream.of("this")
                    ).toList();
                    return methodBody.get().body()
                        .lambdaMark(methodType.get().returnTypeName())
                        .substitute(parameterNames, args);
                } else if (
                    this.source() instanceof final FJCast fjCast
                        && fjCast.body() instanceof final FJLambda lambda
                ) {
                    final Optional<FJMethodTypeSignature> methodType = FJUtils.methodType(
                        classTable,
                        this.methodName(),
                        fjCast.typeName()
                    );
                    if (methodType.isEmpty()) return Optional.empty(); // No method type
                    final List<FJExpr> args = new ArrayList<>();
                    // <=> zip(this.args, methodType.parameterTypeNames)
                    for (int i = 0; i < this.args().size(); i++) {
                        final FJExpr arg = this.args().get(i);
                        final String typeName = methodType.get().parameterTypeNames().get(i);
                        args.add(arg.lambdaMark(typeName));
                    }

                    final Optional<FJMethodBodySignature> methodBody = FJUtils.methodBody(
                        classTable,
                        this.methodName(),
                        fjCast.typeName()
                    );
                    if (methodBody.isPresent()) { // R-Default
                        return methodBody.get().body()
                            .lambdaMark(methodType.get().returnTypeName())
                            .substitute(methodBody.get().argumentNames(), args);
                    } else { // R-Lam
                        return lambda.body()
                            .lambdaMark(methodType.get().returnTypeName())
                            .substitute(lambda.args().stream().map(FJField::name).toList(), args);
                    }
                } else {
                    return Optional.empty();
                }
            } else { // RC-Invk-Arg
                final List<FJExpr> args = this.args().stream().map(e -> e._eval(classTable))
                    .flatMap(Optional::stream).toList();
                return Optional.of(new FJMethodInvocation(
                    this.source(),
                    this.methodName(),
                    args
                ));
            }
        } else { // RC-Invk-Recv
            return this.source()._eval(classTable)
                .map(e -> new FJMethodInvocation(e, this.methodName(), this.args()));
        }
    }

    @Override
    public Optional<FJExpr> substitute(List<String> parameterNames, List<FJExpr> args) {
        return this.source().substitute(parameterNames, args)
            .map(e -> {
                final List<FJExpr> _args = this.args().stream()
                    .map(a -> a.substitute(parameterNames, args))
                    .flatMap(Optional::stream).toList();
                return new FJMethodInvocation(e, this.methodName(), _args);
            });
    }
}
