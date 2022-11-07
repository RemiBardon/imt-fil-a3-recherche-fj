package imt.fil.a3.recherche.fj.v2;

import imt.fil.a3.recherche.fj.FJUtils;
import imt.fil.a3.recherche.fj.parser.FJMethod;
import imt.fil.a3.recherche.fj.parser.error.ClassNotFound;
import imt.fil.a3.recherche.fj.parser.error.FieldNotFound;
import imt.fil.a3.recherche.fj.parser.error.TypeError;
import imt.fil.a3.recherche.fj.parser.error.VariableNotFound;
import imt.fil.a3.recherche.fj.parser.expression.FJExpr;
import imt.fil.a3.recherche.fj.parser.expression.FJFieldAccess;
import imt.fil.a3.recherche.fj.parser.expression.FJVariable;
import imt.fil.a3.recherche.fj.parser.type.FJClass;
import imt.fil.a3.recherche.fj.parser.type.FJInterface;
import imt.fil.a3.recherche.fj.parser.type.FJType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

public final class FJTypeChecker {
    final HashMap<String, FJType> classTable;
    final HashMap<String, String> context;

    public FJTypeChecker(HashMap<String, FJType> classTable, HashMap<String, String> context) {
        this.classTable = classTable;
        this.context = context;
    }

    /**
     * Checks the type of a given expression.
     * @return The type of a given term or a type error.
     */
    String typeNameOf(FJExpr expression) throws TypeError {
        if (expression instanceof final FJVariable variable) { // T-Var
            final String varName = variable.name;
            if (this.context.containsKey(varName)) {
                return this.context.get(varName);
            } else {
                throw new VariableNotFound(varName);
            }
        } else if (expression instanceof final FJFieldAccess fieldAccess) { // T-Field
            final String typeName = this.typeNameOf(fieldAccess.object);

            final var fields = FJUtils.classFields(this.classTable, typeName);
            if (fields.isEmpty()) {
                throw new ClassNotFound(typeName);
            }

            // NOTE: `filter` iterates over all elements while we could abort sooner if a value is found.
            // TODO: Find a way to avoid unnecessary filtering.
            final var field = fields.get().stream()
                .filter(f -> Objects.equals(f.name, fieldAccess.fieldName))
                .findFirst();
            if (field.isPresent()) {
                return field.get().type;
            } else {
                throw new FieldNotFound(fieldAccess.fieldName);
            }
        }
        throw new RuntimeException("Not implemented yet.");
    }

    /**
     * Checks if a method is well formed.
     * @return `Boolean.TRUE` for a well formed method, `Boolean.FALSE` otherwise.
     **/
    public Boolean methodTyping(String className, FJMethod method) {
        throw new RuntimeException("Not implemented yet.");
    }

    /**
     * Checks if a class is well-formed.
     * @return `Boolean.TRUE` for a well-formed class, `Boolean.FALSE` otherwise.
     */
    public Boolean classTyping(FJClass fjClass) {
        throw new RuntimeException("Not implemented yet.");
    }

    /**
     * Checks if an interface is well-formed.
     * @return `Boolean.TRUE` for a well-formed interface, `Boolean.FALSE` otherwise.
     */
    public Boolean interfaceTyping(FJInterface fjInterface) {
        throw new RuntimeException("Not implemented yet.");
    }
}
