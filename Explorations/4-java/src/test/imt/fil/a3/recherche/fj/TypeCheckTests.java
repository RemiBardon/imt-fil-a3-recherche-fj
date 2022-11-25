package imt.fil.a3.recherche.fj;

import imt.fil.a3.recherche.fj.model.TypeCheckingContext;
import imt.fil.a3.recherche.fj.model.TypeTable;
import imt.fil.a3.recherche.fj.model.error.TypeError;
import imt.fil.a3.recherche.fj.model.error.VariableNotFound;
import imt.fil.a3.recherche.fj.model.java.expression.*;
import imt.fil.a3.recherche.fj.model.java.type.FJInterface;
import imt.fil.a3.recherche.fj.util.builder.error.FJBuilderException;
import imt.fil.a3.recherche.fj.util.builder.model.expression.*;
import imt.fil.a3.recherche.fj.util.builder.model.type.FJInterfaceBuilder;
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

    @Test
    void testTNew() throws FJBuilderException, TypeError {
        final FJCreateObject createObject = new FJCreateObjectBuilder()
            .className("A")
            .build();

        this.typeTable.add(FJTests.classA().build());

        Assertions.assertEquals("A", createObject.getTypeApproach1(this.context).typeName());
        Assertions.assertEquals("A", createObject.getTypeNameApproach2(this.context));
    }

    @Test
    void testTLam() throws FJBuilderException, TypeError {
        //(A) (x : A) -> x
        final FJCast fjCast = new FJCastBuilder()
            .typeName("A")
            .body(eb -> eb
                .lambda(lb -> lb
                    .arg(fb -> fb
                        .name("x")
                        .type("A")
                    )
                    .arg(fb -> fb
                            .name("y")
                            .type("A")
                    )
                    .body(eb2 -> eb2
                        .variable(vb -> vb
                            .name("x")
                        )
                    )
                )
            )
            .build();

        FJInterface fjInterface = new FJInterfaceBuilder()
                .name("A")
                .signature(sb -> sb
                        .returns("A")
                                .arg("A","a")
                                .arg("A","b")
                        )
                .build();

        this.typeTable.add(fjInterface);

        Assertions.assertEquals("(A,A)->A", fjCast.getTypeApproach1(this.context).typeName());
        Assertions.assertEquals("(A,A)->A", fjCast.getTypeNameApproach2(this.context));

    }

    @Test
    void testUCast() throws FJBuilderException, TypeError {

        // (A) (x : A)
        FJCast fjCast = new FJCastBuilder()
            .typeName("A")
            .body(eb -> eb
                .variable(vb -> vb
                    .name("x")
                )
            )
            .build();


        this.typeTable.add(FJTests.classA().build());
        this.context.add("x", "A");

        Assertions.assertEquals("A", fjCast.getTypeApproach1(this.context).typeName());
        Assertions.assertEquals("A", fjCast.getTypeNameApproach2(this.context));
    }
}
