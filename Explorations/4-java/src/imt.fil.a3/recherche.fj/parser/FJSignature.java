package imt.fil.a3.recherche.fj.parser;

import java.util.List;
import java.util.stream.Collectors;

public final class FJSignature {
    public final String returnTypeName;
    public final String name;
    public final List<FJField> args;

    public FJSignature(String returnTypeName, String name, List<FJField> args) {
        this.returnTypeName = returnTypeName;
        this.name = name;
        this.args = args;
    }

    public FJMethodTypeSignature getTypeSignature() {
        return new FJMethodTypeSignature(
            this.args.stream().map(f -> f.type).collect(Collectors.toList()),
            this.returnTypeName
        );
    }
}
