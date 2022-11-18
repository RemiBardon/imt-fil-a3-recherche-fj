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
                .constructor(cb -> cb
                    .name("A")
                )
            )
            .clazz(c -> c
                .name("B")
                .constructor(cb -> cb
                    .name("B")
                )
            )
            .clazz(c -> c
                .name("Pair")
                .constructor(cb -> cb
                    .name("Pair")
                    .arg(fb -> fb.type("A").name("fst"))
                    .arg(fb -> fb.type("B").name("snd"))
                    .fieldInit(fib -> fib
                        .fieldName("fst")
                        .argumentName("fst")
                    )
                    .fieldInit(fib -> fib
                        .fieldName("snd")
                        .argumentName("snd")
                    )
                )
                .field(fb -> fb.type("A").name("fst"))
                .field(f -> f.type("B").name("snd"))
                .method(mb -> mb
                    .signature(sb -> sb
                        .returnTypeName("Pair")
                        .name("setfst")
                        .arg(fb -> fb.type("A").name("newfst"))
                    )
                    // return new Pair ( newfst , this . snd )
                    .body(eb -> eb
                        .createObject(cob -> cob
                            .className("Pair")
                            .arg(ebArg -> ebArg
                                .variable(vb -> vb.name("newfst"))
                            )
                            .arg(ebArg -> ebArg
                                .fieldAccess(fab -> fab
                                    .fieldName("snd")
                                    .object(ebObject -> ebObject
                                        .variable(vb -> vb.name("this"))
                                    )
                                )
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
