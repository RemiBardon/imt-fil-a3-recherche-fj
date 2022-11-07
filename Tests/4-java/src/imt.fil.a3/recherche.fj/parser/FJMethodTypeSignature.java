package imt.fil.a3.recherche.fj.parser;

import java.util.List;

public final class FJMethodTypeSignature {
    public final List<String> argumentTypeNames;
    public final String returnTypeName;

    public FJMethodTypeSignature(List<String> argumentTypeNames, String returnTypeName) {
        this.argumentTypeNames = argumentTypeNames;
        this.returnTypeName = returnTypeName;
    }
}
