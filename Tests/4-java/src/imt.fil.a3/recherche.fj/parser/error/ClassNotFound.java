package imt.fil.a3.recherche.fj.parser.error;

public final class ClassNotFound implements TypeError {
    public final String name;

    public ClassNotFound(String name) {
        this.name = name;
    }
}
