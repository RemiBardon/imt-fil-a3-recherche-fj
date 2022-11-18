package imt.fil.a3.recherche.fj;

import imt.fil.a3.recherche.fj.model.TypeCheckingContext;
import imt.fil.a3.recherche.fj.model.java.misc.FJProgram;
import imt.fil.a3.recherche.fj.util.builder.FJProgramBuilder;
import imt.fil.a3.recherche.fj.util.builder.error.FJBuilderException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

@SuppressWarnings("SpellCheckingInspection")
final class FJTests {

    @Test
    void testPaperExampleTypeChecks() {
        final FJProgramBuilder programBuilder = new FJProgramBuilder()
            .clazz(c -> c
                .name("A")
                .constructor()
            )
            .clazz(c -> c
                .name("B")
                .constructor()
            )
            .clazz(c -> c
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
                )
            );
        try {
            FJProgram program = programBuilder.build();
            final var context = new TypeCheckingContext(program.getTypeTable(), new HashMap<>());
            Assertions.assertTrue(program.typeCheck(context));
        } catch (FJBuilderException e) {
            Assertions.fail(e);
        }
    }

}
