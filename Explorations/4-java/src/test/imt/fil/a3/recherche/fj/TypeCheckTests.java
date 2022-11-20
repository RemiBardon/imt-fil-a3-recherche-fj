package imt.fil.a3.recherche.fj;

import imt.fil.a3.recherche.fj.model.TypeCheckingContext;
import imt.fil.a3.recherche.fj.model.TypeTable;
import imt.fil.a3.recherche.fj.model.error.TypeError;
import imt.fil.a3.recherche.fj.model.error.VariableNotFound;
import imt.fil.a3.recherche.fj.model.java.expression.FJFieldAccess;
import imt.fil.a3.recherche.fj.model.java.expression.FJMethodInvocation;
import imt.fil.a3.recherche.fj.model.java.expression.FJVariable;
import imt.fil.a3.recherche.fj.util.builder.model.expression.FJFieldAccessBuilder;
import imt.fil.a3.recherche.fj.util.builder.model.expression.FJMethodInvocationBuilder;
import imt.fil.a3.recherche.fj.util.builder.model.expression.FJVariableBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

@SuppressWarnings("SpellCheckingInspection")
public class TypeCheckTests {

    private TypeTable typeTable ;
    private TypeCheckingContext context ;

    @BeforeEach
    void init() {
        typeTable = new TypeTable(new HashMap<>());
        context = new TypeCheckingContext(typeTable, new HashMap<>());
    }


    //TVAR

    @Test
    void testTVar() throws TypeError {
        FJVariable XVariable = new FJVariableBuilder()
                .name("x")
                .build();

        context = context.with("x", "String");

        Assertions.assertEquals("String", XVariable.getTypeNameApproach2(context));
    }

    @Test
    void testTVar_fail_when_varialbe_not_defined(){
        FJVariable XVariable = new FJVariableBuilder()
                .name("x")
                .build();

        Assertions.assertThrows(VariableNotFound.class, () -> XVariable.getTypeNameApproach2(context));
    }

    //TFIELD
    @Test
    void testTField() throws TypeError {
        /*
        FJFieldAccess fjFieldAccess = new FJFieldAccessBuilder()
                .fieldName()
        context = context.with("x", "String");

        Assertions.assertEquals("String", XVariable.getTypeNameApproach2(context));

         */
    }

    @Test
    void testTInvk(){
        FJMethodInvocation invocation = new FJMethodInvocationBuilder()
                .methodName("setfst")
                .arg("A", "newfst")
                .build();
    }
}
