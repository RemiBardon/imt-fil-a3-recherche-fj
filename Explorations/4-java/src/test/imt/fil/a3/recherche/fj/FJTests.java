package imt.fil.a3.recherche.fj;

import imt.fil.a3.recherche.fj.model.TypeCheckingContext;
import imt.fil.a3.recherche.fj.model.TypeTable;
import imt.fil.a3.recherche.fj.model.java.misc.FJProgram;
import imt.fil.a3.recherche.fj.util.builder.FJProgramBuilder;
import imt.fil.a3.recherche.fj.util.builder.error.FJBuilderException;

import java.util.HashMap;

public class FJTests {

    public static void main(String[] args) {
        try {
            FJProgram program = new FJProgramBuilder()
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
                        .arg(fb -> fb
                            .name("fst")
                            .type("X")
                        )
                        .arg(fb -> fb
                            .name("snd")
                            .type("Y")
                        )
                        .fieldInit(fib -> fib
                            .fieldName("fst")
                            .argumentName("fst")
                        )
                        .fieldInit(fib -> fib
                            .fieldName("snd")
                            .argumentName("snd")
                        )
                    )
                    .field(fb -> fb
                        .name("fst")
                        .type("X")
                    )
                    .field(f -> f.type("Y").name("snd"))
                    .method(mb -> mb
                        .signature(sb -> sb
                            .name("setfst")
                            .returnTypeName("Pair")
                            .arg(fb -> fb
                                .name("newfst")
                                .type("Object")
                            )
                        )
                        // return new Pair ( newfst , this . snd )
                        .body(eb -> eb
                            .createObject(cob -> cob
                                .className("Pair")
                                .arg(ebArg -> ebArg
                                    .variable(vb -> vb
                                        .name("newfst")
                                    )
                                )
                                .arg(ebArg -> ebArg
                                    .fieldAccess(fab -> fab
                                        .fieldName("snd")
                                        .object(ebObject -> ebObject
                                            .variable(vb -> vb
                                                .name("this")
                                            )
                                        )
                                    )
                                )

                            )
                        )
                    )
                )
                .build();
            final var context = new TypeCheckingContext(new TypeTable(new HashMap<>()), new HashMap<>());
            assert program.typeCheck(context);
        } catch (FJBuilderException e) {
            e.printStackTrace();
            assert false;
        }
    }

}
