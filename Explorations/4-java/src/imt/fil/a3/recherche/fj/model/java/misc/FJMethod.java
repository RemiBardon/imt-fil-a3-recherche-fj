package imt.fil.a3.recherche.fj.model.java.misc;

import imt.fil.a3.recherche.fj.model.error.TypeError;
import imt.fil.a3.recherche.fj.model.java.expression.FJExpr;
import imt.fil.a3.recherche.fj.model.java.type.FJType;
import imt.fil.a3.recherche.fj.model.misc.MethodBodySignature;
import imt.fil.a3.recherche.fj.util.FJUtils;

import java.util.HashMap;
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
        final HashMap<String, FJType> classTable,
        final HashMap<String, String> context,
        final String className
    ) {
        // TODO: Store a reference to the class in the object,
        //       so it can update its context without extra logic outside.
        HashMap<String, String> methodContext = new HashMap<>(context);
        for (FJField arg : this.signature.args()) {
            methodContext.put(arg.name(), arg.type());
        }
        methodContext.put("this", className);

        final String expectedReturnTypeName;
        try {
            expectedReturnTypeName = this.body.lambdaMark(this.signature.returnTypeName())
                .getTypeName(classTable, methodContext);
        } catch (TypeError e) {
            return false; // Error obtaining type of expression
        }

        final Optional<List<FJMethod>> methods = FJUtils.methods(classTable, expectedReturnTypeName);
        if (methods.isEmpty()) return false; // Error obtaining methods
        return FJUtils.isSubtype(classTable, expectedReturnTypeName, this.signature.returnTypeName())
            && methods.get().contains(this);
    }

    // NOTE: This warning is a bogus, the method is used, through a reference.
    @SuppressWarnings("unused")
    public Boolean signatureEquals(FJMethod other) {
        return this.signature.name().equals(other.signature.name());
    }
}
