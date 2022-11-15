package imt.fil.a3.recherche.fj.util.haskell;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Haskell {
    /**
     * Translation of <a href="https://hackage.haskell.org/package/base-4.14.1.0/docs/Data-List.html#v:union">Haskell's union function</a>.
     */
    public static <E> Stream<E> union(Stream<E> a, Stream<E> b) {
        return union(a, b, E::equals);
    }

    /**
     * Translation of <a href="https://hackage.haskell.org/package/base-4.14.1.0/docs/Data-List.html#v:unionBy">Haskell's unionBy function</a>.
     */
    public static <E> Stream<E> union(Stream<E> a, Stream<E> b, BiPredicate<E, E> predicate) {
        // > Duplicates are removed from the the second list
        List<E> b1 = b.distinct().collect(Collectors.toList());
        // > elements of the first list are removed from the the second list
        a.distinct().forEach(e1 -> b1.removeIf(e2 -> predicate.test(e1, e2)));
        // > if the first list contains duplicates, so will the result
        return Stream.concat(a, b1.stream());
    }

    /**
     * Translation of <a href="https://hackage.haskell.org/package/base-4.17.0.0/docs/Data-List.html#v:-92--92-">Haskell's \\ function</a>.
     */
    public static <E> List<E> difference(List<E> a, List<E> b) {
        List<E> res = new ArrayList<>(a);
        for (final E e : b) {
            final int index = res.indexOf(e);
            if (index > -1) res.remove(index);
        }
        return res;
    }
}
