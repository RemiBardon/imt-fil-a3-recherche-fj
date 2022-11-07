package imt.fil.a3.recherche.fj.parser.error;

public final class VariableNotFound extends TypeError {
    public final String name;

    public VariableNotFound(String name) {
        this.name = name;
    }
}
