package imt.fil.a3.recherche.fj.parser.error;

public final class ClassNotFound extends TypeError {
    public final String name;

    public ClassNotFound(String name) {
        this.name = name;
    }
}
