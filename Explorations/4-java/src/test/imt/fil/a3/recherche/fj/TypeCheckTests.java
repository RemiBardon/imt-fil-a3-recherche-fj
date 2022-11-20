package imt.fil.a3.recherche.fj;

import imt.fil.a3.recherche.fj.model.TypeCheckingContext;
import imt.fil.a3.recherche.fj.model.TypeTable;
import imt.fil.a3.recherche.fj.model.error.TypeError;
import imt.fil.a3.recherche.fj.model.error.VariableNotFound;
import imt.fil.a3.recherche.fj.model.java.expression.FJFieldAccess;
import imt.fil.a3.recherche.fj.model.java.expression.FJMethodInvocation;
import imt.fil.a3.recherche.fj.model.java.expression.FJVariable;
import imt.fil.a3.recherche.fj.util.builder.error.FJBuilderException;
import imt.fil.a3.recherche.fj.util.builder.model.expression.FJFieldAccessBuilder;
import imt.fil.a3.recherche.fj.util.builder.model.expression.FJMethodInvocationBuilder;
import imt.fil.a3.recherche.fj.util.builder.model.expression.FJVariableBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

@SuppressWarnings("SpellCheckingInspection")
public final class TypeCheckTests {
    private TypeTable typeTable;
    private TypeCheckingContext context;

    @BeforeEach
    void init() {
        this.typeTable = new TypeTable(new HashMap<>());
        this.context = new TypeCheckingContext(this.typeTable, new HashMap<>());
    }

    /**
     * T-Var
     */
    @Test
    void testTVar() throws TypeError {
        final FJVariable xVariable = new FJVariableBuilder().name("x").build();

        this.context.add("x", "String");

        Assertions.assertEquals("String", xVariable.getTypeApproach1(this.context).typeName());
        Assertions.assertEquals("String", xVariable.getTypeNameApproach2(this.context));
    }

    /**
     * T-Var
     */
    @Test
    void testTVar_fail_when_varialbe_not_defined() {
        final FJVariable xVariable = new FJVariableBuilder().name("x").build();

        Assertions.assertThrows(VariableNotFound.class, () -> xVariable.getTypeApproach1(this.context));
        Assertions.assertThrows(VariableNotFound.class, () -> xVariable.getTypeNameApproach2(this.context));
    }

    /**
     * T-Field
     */
    @Test
    void testTField() throws FJBuilderException, TypeError {
        final FJFieldAccess fieldAccess = new FJFieldAccessBuilder()
            .object(ob -> ob.variable(vb -> vb.name("this")))
            .fieldName("fst")
            .build();

        this.typeTable.add(FJTests.classA().build());
        this.typeTable.add(FJTests.classB().build());
        this.typeTable.add(FJTests.classPair().build());
        this.context.add("this", "Pair");

        Assertions.assertEquals("A", fieldAccess.getTypeApproach1(this.context).typeName());
        Assertions.assertEquals("A", fieldAccess.getTypeNameApproach2(this.context));
    }

    /**
     * T-Invk
     */
    @Test
    void testTInvk() throws FJBuilderException, TypeError {
        final FJMethodInvocation invocation = new FJMethodInvocationBuilder()
            .source(sb -> sb
                .variable(vb -> vb.name("pair"))
            )
            .methodName("setfst")
            .arg(eb -> eb
                .createObject(ob -> ob
                    .className("A")
                )
            )
            .build();

        this.typeTable.add(FJTests.classA().build());
        this.typeTable.add(FJTests.classB().build());
        this.typeTable.add(FJTests.classPair().build());
        this.context.add("pair", "Pair");

        Assertions.assertEquals("Pair", invocation.getTypeApproach1(this.context).typeName());
        Assertions.assertEquals("Pair", invocation.getTypeNameApproach2(this.context));
    }
}
