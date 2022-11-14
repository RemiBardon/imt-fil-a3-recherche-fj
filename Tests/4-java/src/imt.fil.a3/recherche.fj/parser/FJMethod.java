package imt.fil.a3.recherche.fj.parser;

import imt.fil.a3.recherche.fj.FJUtils;
import imt.fil.a3.recherche.fj.parser.error.TypeError;
import imt.fil.a3.recherche.fj.parser.expression.FJExpr;
import imt.fil.a3.recherche.fj.parser.type.FJType;
import imt.fil.a3.recherche.fj.v2.FJTypeChecker;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public final class FJMethod {
    public final FJSignature signature;
    public final FJExpr body;

    public FJMethod(FJSignature signature, FJExpr body) {
        this.signature = signature;
        this.body = body;
    }

    /**
     * Checks if a method is well formed.
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
        for (FJField arg: this.signature.args) {
            methodContext.put(arg.name, arg.type);
        }
        methodContext.put("this", className);

        final String expectedReturnTypeName;
        try {
            expectedReturnTypeName = new FJTypeChecker(classTable, methodContext)
                .typeNameOf(FJUtils.lambdaMark(this.body, this.signature.returnTypeName));
        } catch (TypeError e) {
            return false; // Error obtaining type of expression
        }

        final Optional<List<FJMethod>> methods = FJUtils.methods(classTable, expectedReturnTypeName);
        if (methods.isEmpty()) {
            return false; // Error obtaining methods
        }
        return FJUtils.isSubtype(classTable, expectedReturnTypeName, this.signature.returnTypeName)
            && methods.get().contains(this);
    }
}
