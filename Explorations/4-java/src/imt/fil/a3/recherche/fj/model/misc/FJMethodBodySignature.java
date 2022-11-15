package imt.fil.a3.recherche.fj.model.misc;

import imt.fil.a3.recherche.fj.model.java.expression.FJExpr;

import java.util.List;

public record FJMethodBodySignature(List<String> argumentNames, FJExpr body) {
}
