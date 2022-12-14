package imt.fil.a3.recherche.fj.model.error;

public final class VariableNotFound extends TypeError {
    public final String name;

    public VariableNotFound(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return "Variable `" + this.name + "` not found.";
    }
}
