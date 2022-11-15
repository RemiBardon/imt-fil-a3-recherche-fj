package imt.fil.a3.recherche.fj.model.error;

public final class MethodNotFound extends TypeError {
    public final String name;
    public final String returnTypeName;

    public MethodNotFound(String name, String returnTypeName) {
        this.name = name;
        this.returnTypeName = returnTypeName;
    }
}
