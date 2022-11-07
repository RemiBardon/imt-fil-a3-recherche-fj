package imt.fil.a3.recherche.fj.parser;

import java.util.List;

public final class FJSignature {
    public final String typeName;
    public final String name;
    public final List<FJField> args;

    public FJSignature(
            String typeName,
            String name,
            List<FJField> args
    ) {
        this.typeName = typeName;
        this.name = name;
        this.args = args;
    }
}
