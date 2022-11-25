package imt.fil.a3.recherche.fj.model.java.misc;

import imt.fil.a3.recherche.fj.model.TypeCheckingContext;
import imt.fil.a3.recherche.fj.model.TypeTable;
import imt.fil.a3.recherche.fj.model.error.ClassNotFound;
import imt.fil.a3.recherche.fj.model.java.type.FJType;

import java.util.HashMap;
import java.util.List;

public record FJProgram(List<FJType> types) {
    public Boolean typeCheckApproach2(final TypeCheckingContext context) throws ClassNotFound {
        boolean allTypesAreTyped = true;
        for (FJType type : this.types) {
            allTypesAreTyped &= type.typeCheckApproach2(context);
        }
        return allTypesAreTyped;
    }

    public Boolean typeCheckApproach1(final TypeCheckingContext context) throws ClassNotFound {
        boolean allTypesAreTyped = true;
        for (FJType type : this.types) {
            allTypesAreTyped &= type.typeCheckApproach1(context).isPresent();
        }
        return allTypesAreTyped;
    }

    public TypeTable getTypeTable() {
        final var map = new HashMap<String, FJType>();
        this.types.forEach(t -> map.put(t.name(), t));
        return new TypeTable(map);
    }
}
