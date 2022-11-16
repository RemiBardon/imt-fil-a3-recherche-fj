package imt.fil.a3.recherche.fj.model.java.misc;

import imt.fil.a3.recherche.fj.model.TypeCheckingContext;
import imt.fil.a3.recherche.fj.model.java.type.FJType;
import java.util.List;

public record FJProgram(List<FJType> types) {
    public Boolean typeCheck(final TypeCheckingContext context) {
        return types.stream().allMatch(type -> type.typeCheck(context));
    }
}
