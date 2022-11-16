package imt.fil.a3.recherche.fj;

import imt.fil.a3.recherche.fj.model.TypeCheckingContext;
import imt.fil.a3.recherche.fj.model.java.misc.FJProgram;
import imt.fil.a3.recherche.fj.util.builder.FJProgramBuilder;
import imt.fil.a3.recherche.fj.util.builder.error.FJBuilderException;

import java.util.HashMap;

@SuppressWarnings("SpellCheckingInspection")
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
                )
                .build();
            final var context = new TypeCheckingContext(program.getTypeTable(), new HashMap<>());
            System.out.println(program.typeCheck(context));
        } catch (FJBuilderException e) {
            e.printStackTrace();
            assert false;
        }
    }

}
