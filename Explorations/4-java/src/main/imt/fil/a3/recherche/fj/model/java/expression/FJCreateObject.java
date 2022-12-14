package imt.fil.a3.recherche.fj.model.java.expression;

import imt.fil.a3.recherche.fj.model.TypeCheckingContext;
import imt.fil.a3.recherche.fj.model.TypeTable;
import imt.fil.a3.recherche.fj.model.error.ArgTypeMismatch;
import imt.fil.a3.recherche.fj.model.error.ArgsTypesMismatch;
import imt.fil.a3.recherche.fj.model.error.ClassNotFound;
import imt.fil.a3.recherche.fj.model.error.TypeError;
import imt.fil.a3.recherche.fj.model.java.misc.FJField;
import imt.fil.a3.recherche.fj.model.misc.MethodBodySignature;
import imt.fil.a3.recherche.fj.model.misc.MethodTypeSignature;
import imt.fil.a3.recherche.fj.model.misc.TypeAnnotatedExpression;
import imt.fil.a3.recherche.fj.model.misc.TypeMismatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public record FJCreateObject(
    String className,
    List<FJExpr> args
) implements FJExpr {
    @Override
    public TypeAnnotatedExpression getTypeApproach1(final TypeCheckingContext context) throws TypeError { // T-New
        final Optional<List<FJField>> fields = context.typeTable.classFields(this.className);
        if (fields.isEmpty()) throw new ClassNotFound(this.className);

        // Make sure the correct number of arguments are passed
        if (this.args.size() != fields.get().size()) {
            throw new ArgsTypesMismatch(fields.get().stream().map(FJField::type).toList(), this.args, context);
        }

        // <=> zip(this.args.map(lambdaMark), fields)
        final var temp = new ArrayList<TypeMismatch>();
        for (int i = 0; i < this.args.size(); i++) {
            final FJExpr arg = this.args.get(i);
            final FJField field = fields.get().get(i);
            temp.add(new TypeMismatch(arg.lambdaMark(field.type()), field.type()));
        }

        // Check that arguments are correctly typed
        final var elaboratedArgs = new ArrayList<FJExpr>();
        for (final TypeMismatch tm : temp) {
            final TypeAnnotatedExpression ae = tm.expression().getTypeApproach1(context);
            elaboratedArgs.add(ae.expression());
            if (!context.typeTable.isSubtype(ae.typeName(), tm.expectedTypeName())) {
                throw new ArgTypeMismatch(tm.expectedTypeName(), ae.typeName());
            }
        }

        // Object creation is correctly typed
        return new TypeAnnotatedExpression(this.className, new FJCreateObject(this.className, elaboratedArgs));
    }

    @Override
    public String getTypeNameApproach2(final TypeCheckingContext context) throws TypeError { // T-New
        final Optional<List<FJField>> fields = context.typeTable.classFields(this.className);
        if (fields.isEmpty()) throw new ClassNotFound(this.className);
        if (this.args.size() != fields.get().size()) {
            throw new ArgsTypesMismatch(fields.get().stream().map(FJField::type).toList(), this.args, context);
        }

        var temp = new ArrayList<TypeMismatch>();
        for (int i = 0; i < this.args.size(); i++) {
            final FJExpr arg = this.args.get(i);
            final FJField field = fields.get().get(i);
            temp.add(new TypeMismatch(arg.lambdaMark(field.type()), field.type()));
        }

        // Check object creation arguments typing
        for (final TypeMismatch tm : temp) {
            final String type = tm.expression().getTypeNameApproach2(context);
            if (!context.typeTable.isSubtype(type, tm.expectedTypeName())) {
                throw new ArgTypeMismatch(tm.expectedTypeName(), type);
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
    public Optional<FJExpr> _evalApproach2(final TypeTable typeTable) throws ClassNotFound { // RC-New-Arg
        //eq of : final List<FJExpr> args = this.args.stream().map(e -> e.evalApproach2(typeTable)).toList();
        List<FJExpr> args = new ArrayList<>();
        for (FJExpr arg : this.args) {
            args.add(arg.evalApproach2(typeTable));
        }
        return Optional.of(new FJCreateObject(this.className, args));
    }

    @Override
    public Optional<FJExpr> substituteApproach2(final List<String> parameterNames, final List<FJExpr> args) {
        final List<FJExpr> _args = this.args.stream()
            .map(a -> a.substituteApproach2(parameterNames, args))
            .flatMap(Optional::stream).toList();
        return Optional.of(new FJCreateObject(this.className, _args));
    }

    @Override
    public Optional<FJExpr> evalMethodInvocationApproach2(
        final TypeTable typeTable,
        final FJMethodInvocation invocation
    ) {
        // R-Invk
        final Optional<MethodTypeSignature> methodType = typeTable.methodType(invocation.methodName(), this.className);
        if (methodType.isEmpty()) return Optional.empty(); // No method type

        final Optional<MethodBodySignature> methodBody = typeTable.methodBody(invocation.methodName(), this.className);
        if (methodBody.isEmpty()) return Optional.empty(); // No method body

        final List<FJExpr> args = new ArrayList<>();
        // <=> zip(methodArgs, methodType.parameterTypeNames)
        for (int i = 0; i < invocation.args().size(); i++) {
            final FJExpr arg = invocation.args().get(i);
            final String typeName = methodType.get().parameterTypeNames().get(i);
            args.add(arg.lambdaMark(typeName));
        }
        args.add(invocation.source());
        final List<String> parameterNames = Stream.concat(
            methodBody.get().argumentNames().stream(),
            Stream.of("this")
        ).toList();
        return methodBody.get().body()
            .lambdaMark(methodType.get().returnTypeName())
            .substituteApproach2(parameterNames, args);
    }
}
