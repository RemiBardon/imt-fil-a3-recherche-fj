package imt.fil.a3.recherche.fj.parser.error;

public final class FieldNotFound implements TypeError {
    public final String name;

    public FieldNotFound(String name) {
        this.name = name;
    }
}
