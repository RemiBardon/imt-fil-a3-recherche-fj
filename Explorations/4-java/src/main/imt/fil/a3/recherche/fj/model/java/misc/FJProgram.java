package imt.fil.a3.recherche.fj.model.java.misc;

import imt.fil.a3.recherche.fj.model.TypeCheckingContext;
import imt.fil.a3.recherche.fj.model.TypeTable;
import imt.fil.a3.recherche.fj.model.java.type.FJType;

import java.util.HashMap;
import java.util.List;

public record FJProgram(List<FJType> types) {
    public Boolean typeCheck(final TypeCheckingContext context) {
        return this.types.stream().allMatch(type -> type.typeCheck(context));
    }

    public TypeTable getTypeTable() {
        final var map = new HashMap<String, FJType>();
        this.types.forEach(t -> map.put(t.name(), t));
        return new TypeTable(map);
    }
}
