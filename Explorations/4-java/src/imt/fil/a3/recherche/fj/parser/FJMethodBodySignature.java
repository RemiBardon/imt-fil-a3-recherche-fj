package imt.fil.a3.recherche.fj.parser;

import imt.fil.a3.recherche.fj.parser.expression.FJExpr;

import java.util.List;

public record FJMethodBodySignature(List<String> argumentNames, FJExpr body) {
}
