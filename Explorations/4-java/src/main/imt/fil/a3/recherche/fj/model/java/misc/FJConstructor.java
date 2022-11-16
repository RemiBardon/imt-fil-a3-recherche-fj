package imt.fil.a3.recherche.fj.model.java.misc;

import imt.fil.a3.recherche.fj.model.misc.FieldInit;

import java.util.List;

public record FJConstructor(
    String name,
    List<FJField> args,
    List<String> superArgs,
    @SuppressWarnings("SpellCheckingInspection") List<FieldInit> fieldInits
) {
}
