package imt.fil.a3.recherche.fj.model.error;

public final class ClassNotFound extends TypeError {
    public final String name;

    public ClassNotFound(String name) {
        this.name = name;
    }
}
