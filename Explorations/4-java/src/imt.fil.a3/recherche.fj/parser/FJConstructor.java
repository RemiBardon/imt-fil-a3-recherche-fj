package imt.fil.a3.recherche.fj.parser;

import java.util.List;

public final class FJConstructor {
    public final String name;
    public final List<FJField> args;
    public final List<String> superArgs;
    public final List<FieldInit> fieldInits;

    public FJConstructor(
        String name,
        List<FJField> args,
        List<String> superArgs,
        List<FieldInit> fieldInits
    ) {
        this.name = name;
        this.args = args;
        this.superArgs = superArgs;
        this.fieldInits = fieldInits;
    }
}
