package imt.fil.a3.recherche.fj.parser;

import java.util.List;

public final class FJConstructor {
    public final String name;
    public final List<FJField> args;
    public final List<String> superArgs;
    @SuppressWarnings("SpellCheckingInspection")
    public final List<FieldInit> fieldInits;

    public FJConstructor(
        final String name,
        final List<FJField> args,
        final List<String> superArgs,
        @SuppressWarnings("SpellCheckingInspection") final List<FieldInit> fieldInits
    ) {
        this.name = name;
        this.args = args;
        this.superArgs = superArgs;
        this.fieldInits = fieldInits;
    }
}
