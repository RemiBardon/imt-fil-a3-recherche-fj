package imt.fil.a3.recherche.fj.parser.error;

public final class MethodNotFound implements TypeError {
    public final String name;
    public final String returnTypeName;

    public MethodNotFound(String name, String returnTypeName) {
        this.name = name;
        this.returnTypeName = returnTypeName;
    }
}
