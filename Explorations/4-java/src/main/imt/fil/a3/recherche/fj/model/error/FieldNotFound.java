package imt.fil.a3.recherche.fj.model.error;

public final class FieldNotFound extends TypeError {
    public final String name;

    public FieldNotFound(String name) {
        this.name = name;
    }

    @Override
    public String getMessage() {
        return "Field `" + this.name + "` not found.";
    }
}
