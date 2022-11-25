package imt.fil.a3.recherche.fj;

import imt.fil.a3.recherche.fj.model.TypeCheckingContext;
import imt.fil.a3.recherche.fj.model.error.ClassNotFound;
import imt.fil.a3.recherche.fj.model.java.misc.FJProgram;
import imt.fil.a3.recherche.fj.util.builder.FJProgramBuilder;
import imt.fil.a3.recherche.fj.util.builder.error.FJBuilderException;
import imt.fil.a3.recherche.fj.util.builder.model.type.FJClassBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

@SuppressWarnings("SpellCheckingInspection")
final class FJTests {
    /**
     * The first example from the paper is not valid Java code, but here is an updated version:
     * <pre><code class="language-java">
     * class A extends Object {
     *   A() { super(); }
     * }
     * class B extends Object {
     *   B() { super(); }
     * }
     * class Pair extends Object {
     *   Object fst;
     *   Object snd;
     *   Pair(A fst, B snd) {
     *     super();
     *     this.fst = fst;
     *     this.snd = snd;
     *   }
     *   Pair setfst(A newfst) {
     *     return new Pair(newfst, this.snd);
     *   }
     * }
     * </code></pre>
     */
    @Test
    void testPaperExampleTypeChecks() {
        final FJProgramBuilder programBuilder = new FJProgramBuilder()
            .clazz(classA())
            .clazz(classB())
            .clazz(classPair());
        try {
            FJProgram program = programBuilder.build();
            final var context = new TypeCheckingContext(program.getTypeTable(), new HashMap<>());
            Assertions.assertTrue(program.typeCheckApproach2(context.copy()));
        } catch (FJBuilderException | ClassNotFound e) {
            Assertions.fail(e);
        }
    }

    static FJClassBuilder classA() {
        return new FJClassBuilder().name("A").constructor();
    }

    static FJClassBuilder classB() {
        return new FJClassBuilder().name("B").constructor();
    }

    static FJClassBuilder classPair() {
        return new FJClassBuilder()
            .name("Pair")
            .constructor(cb -> cb
                .arg("A", "fst")
                .arg("B", "snd")
                .fieldInit("fst")
                .fieldInit("snd")
            )
            .field("A", "fst")
            .field("B", "snd")
            .method(mb -> mb
                .signature(sb -> sb
                    .returns("Pair")
                    .name("setfst")
                    .arg("A", "newfst")
                )
                // <=> `return new Pair (newfst , this.snd)`
                .body(eb -> eb
                    .createObject(cob -> cob
                        .className("Pair")
                        .arg(ebArg -> ebArg
                            .variable(vb -> vb.name("newfst"))
                        )
                        .arg(ebArg -> ebArg
                            .variableFieldAccess("this", "snd")
                        )
                    )
                )
            );
    }
}
