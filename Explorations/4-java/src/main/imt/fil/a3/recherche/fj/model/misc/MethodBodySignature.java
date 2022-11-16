package imt.fil.a3.recherche.fj.model.misc;

import imt.fil.a3.recherche.fj.model.java.expression.FJExpr;

import java.util.List;

public record MethodBodySignature(List<String> argumentNames, FJExpr body) {
}
