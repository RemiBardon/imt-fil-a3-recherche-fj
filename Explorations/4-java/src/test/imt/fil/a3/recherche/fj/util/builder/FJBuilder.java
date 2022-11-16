package imt.fil.a3.recherche.fj.util.builder;

import imt.fil.a3.recherche.fj.util.builder.error.FJBuilderException;

public interface FJBuilder<T> {
    T build() throws FJBuilderException;
}
