package imt.fil.a3.recherche.fj.parser;

import java.util.List;
import java.util.stream.Collectors;

public record FJSignature(String returnTypeName, String name, List<FJField> args) {
    public FJMethodTypeSignature getTypeSignature() {
        return new FJMethodTypeSignature(
            this.args.stream().map(FJField::type).collect(Collectors.toList()),
            this.returnTypeName
        );
    }
}
