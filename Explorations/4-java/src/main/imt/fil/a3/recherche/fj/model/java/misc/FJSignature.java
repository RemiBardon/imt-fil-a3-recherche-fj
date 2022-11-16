package imt.fil.a3.recherche.fj.model.java.misc;

import imt.fil.a3.recherche.fj.model.misc.MethodTypeSignature;

import java.util.List;
import java.util.stream.Collectors;

public record FJSignature(String returnTypeName, String name, List<FJField> args) {
    public MethodTypeSignature getTypeSignature() {
        return new MethodTypeSignature(
            this.args.stream().map(FJField::type).collect(Collectors.toList()),
            this.returnTypeName
        );
    }
}
