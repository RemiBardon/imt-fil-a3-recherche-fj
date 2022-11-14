package imt.fil.a3.recherche.fj.parser;

import java.util.List;

public final class FJMethodTypeSignature {
    public final List<String> parameterTypeNames;
    public final String returnTypeName;

    public FJMethodTypeSignature(List<String> parameterTypeNames, String returnTypeName) {
        this.parameterTypeNames = parameterTypeNames;
        this.returnTypeName = returnTypeName;
    }
}
