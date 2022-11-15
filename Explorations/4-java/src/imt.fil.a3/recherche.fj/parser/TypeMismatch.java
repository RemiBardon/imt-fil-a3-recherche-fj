package imt.fil.a3.recherche.fj.parser;

import imt.fil.a3.recherche.fj.parser.expression.FJExpr;

public record TypeMismatch(FJExpr expression, String expectedTypeName) {
}
