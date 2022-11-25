package imt.fil.a3.recherche.fj.model.error;

public final class ArgTypeMismatch extends TypeError {
    public final String expected;
    public final String actual;

    public ArgTypeMismatch(final String expected, final String actual) {
        this.expected = expected;
        this.actual = actual;
    }

    @Override
    public String getMessage() {
        return "Argument not typed correctly: expected `" + this.expected + "` got `" + this.actual + "`.";
    }
}
