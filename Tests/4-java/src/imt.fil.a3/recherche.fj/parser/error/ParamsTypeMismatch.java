package imt.fil.a3.recherche.fj.parser.error;

import imt.fil.a3.recherche.fj.parser.TypeMismatch;

import java.util.List;

public final class ParamsTypeMismatch implements TypeError {
    public final List<TypeMismatch> params;

    public ParamsTypeMismatch(List<TypeMismatch> params) {
        this.params = params;
    }
}
