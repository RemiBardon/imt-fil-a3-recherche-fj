package imt.fil.a3.recherche.fj.model.misc;

import imt.fil.a3.recherche.fj.model.java.expression.FJExpr;

public record TypeMismatch(FJExpr expression, String expectedTypeName) {
}
