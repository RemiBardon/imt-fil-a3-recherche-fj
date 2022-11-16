package imt.fil.a3.recherche.fj.model.java.misc;

import imt.fil.a3.recherche.fj.model.TypeCheckingContext;
import imt.fil.a3.recherche.fj.model.error.TypeError;
import imt.fil.a3.recherche.fj.model.java.expression.FJExpr;
import imt.fil.a3.recherche.fj.model.misc.MethodBodySignature;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public record FJMethod(FJSignature signature, FJExpr body) {
    public MethodBodySignature getBodySignature() {
        return new MethodBodySignature(
            this.signature.args().stream().map(FJField::name).collect(Collectors.toList()),
            this.body
        );
    }

    /**
     * Checks if a method is well formed.
     *
     * @return {@code Boolean.TRUE} for a well formed method, {@code Boolean.FALSE} otherwise.
     **/
    public Boolean typeCheck(
        final TypeCheckingContext context,
        final String className
    ) {
        final TypeCheckingContext methodContext = context
            .with(this.signature.args())
            .with("this", className);

        final String expectedReturnTypeName;
        try {
            expectedReturnTypeName = this.body.lambdaMark(this.signature.returnTypeName()).getTypeName(methodContext);
        } catch (TypeError e) {
            return false; // Error obtaining type of expression
        }

        final Optional<List<FJMethod>> methods = context.typeTable.methods(expectedReturnTypeName);
        if (methods.isEmpty()) return false; // Error obtaining methods
        return context.typeTable.isSubtype(expectedReturnTypeName, this.signature.returnTypeName())
            && methods.get().contains(this);
    }

    // NOTE: This warning is a bogus, the method is used, through a reference.
    @SuppressWarnings("unused")
    public Boolean signatureEquals(FJMethod other) {
        return this.signature.name().equals(other.signature.name());
    }
}
