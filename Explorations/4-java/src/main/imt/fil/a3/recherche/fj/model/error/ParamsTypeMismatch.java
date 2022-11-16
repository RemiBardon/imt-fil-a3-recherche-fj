package imt.fil.a3.recherche.fj.model.error;

import imt.fil.a3.recherche.fj.model.misc.TypeMismatch;

import java.util.List;

public final class ParamsTypeMismatch extends TypeError {
    public final List<TypeMismatch> params;

    public ParamsTypeMismatch(List<TypeMismatch> params) {
        this.params = params;
    }
}
