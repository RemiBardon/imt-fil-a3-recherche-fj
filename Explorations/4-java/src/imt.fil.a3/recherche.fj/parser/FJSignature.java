package imt.fil.a3.recherche.fj.parser;

import java.util.List;

public final class FJSignature {
    public final String returnTypeName;
    public final String name;
    public final List<FJField> args;

    public FJSignature(String returnTypeName, String name, List<FJField> args) {
        this.returnTypeName = returnTypeName;
        this.name = name;
        this.args = args;
    }
}
