package imt.fil.a3.recherche.fj.model.java.misc;

import imt.fil.a3.recherche.fj.model.TypeCheckingContext;
import imt.fil.a3.recherche.fj.model.error.ClassNotFound;
import imt.fil.a3.recherche.fj.model.error.TypeError;
import imt.fil.a3.recherche.fj.model.java.expression.FJExpr;
import imt.fil.a3.recherche.fj.model.misc.MethodBodySignature;
import imt.fil.a3.recherche.fj.model.misc.TypeAnnotatedExpression;

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
     * @return {@code Optional.empty()} if type check failed,
     * an annotated version of {@code this} if type check succeeded.
     **/
    public Optional<FJMethod> typeCheckApproach1 (
        final TypeCheckingContext context,
        final String className
    ) throws ClassNotFound {
        final TypeCheckingContext methodContext = context
            .with(this.signature.args())
            .with("this", className);

        final TypeAnnotatedExpression typedBody;
        try {
            typedBody = this.body.lambdaMark(this.signature.returnTypeName()).getTypeApproach1(methodContext);
        } catch (TypeError e) {
            TypeCheckingContext.logger.warning("Error obtaining type of expression: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
        final String expectedReturnTypeName = typedBody.typeName();
        final boolean returnTypeIsCorrect = context.typeTable
            .isSubtype(expectedReturnTypeName, this.signature.returnTypeName());
        if (!returnTypeIsCorrect) {
            TypeCheckingContext.logger.info("Method return type is incorrect.");
            return Optional.empty();
        }

        final Optional<List<FJMethod>> methods = context.typeTable.methods(expectedReturnTypeName);
        if (methods.isEmpty()) {
            TypeCheckingContext.logger.warning("Error obtaining methods.");
            return Optional.empty();
        }
        if (!methods.get().contains(this)) {
            TypeCheckingContext.logger.info(
                "Type `" + this.signature.returnTypeName()
                    + "` has no method called `" + this.signature.name() + "`."
            );
            return Optional.empty();
        }

        return Optional.of(new FJMethod(this.signature, typedBody.expression()));
    }

    /**
     * Checks if a method is well formed.
     *
     * @return {@code Boolean.TRUE} for a well formed method, {@code Boolean.FALSE} otherwise.
     **/
    public Boolean typeCheckApproach2(
        final TypeCheckingContext context,
        final String className
    ) throws ClassNotFound {
        final TypeCheckingContext methodContext = context
            .with(this.signature.args())
            .with("this", className);

        final String expectedReturnTypeName;
        try {
            expectedReturnTypeName = this.body.lambdaMark(this.signature.returnTypeName()).getTypeNameApproach2(methodContext);
        } catch (TypeError e) {
            TypeCheckingContext.logger.warning("Error obtaining type of expression: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        final boolean returnTypeIsCorrect = context.typeTable
            .isSubtype(expectedReturnTypeName, this.signature.returnTypeName());
        if (!returnTypeIsCorrect) {
            TypeCheckingContext.logger.info("Method return type is incorrect.");
            return false;
        }

        final Optional<List<FJMethod>> methods = context.typeTable.methods(expectedReturnTypeName);
        if (methods.isEmpty()) {
            TypeCheckingContext.logger.warning("Error obtaining methods.");
            return false;
        }
        if (!methods.get().contains(this)) {
            TypeCheckingContext.logger.info(
                "Type `" + this.signature.returnTypeName()
                    + "` has no method called `" + this.signature.name() + "`."
            );
            return false;
        }

        return true;
    }

    // NOTE: This warning is a bogus, the method is used, through a reference.
    @SuppressWarnings("unused")
    public Boolean signatureEquals(FJMethod other) {
        return this.signature.name().equals(other.signature.name());
    }
}
