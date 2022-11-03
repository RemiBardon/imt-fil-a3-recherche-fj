import Algorithms
import OrderedCollections

/// <https://hackage.haskell.org/package/base-4.14.1.0/docs/Data-List.html#v:union>
func union<E, C1: Collection<E>, C2: Collection<E>>(_ c1: C1, _ c2: C2) -> [E]
where E: Hashable
{
  union(c1, c2, by: ==)
}

/// <https://hackage.haskell.org/package/base-4.14.1.0/docs/Data-List.html#v:unionBy>
func union<E, C1: Collection<E>, C2: Collection<E>>(
  _ c1: C1,
  _ c2: C2,
  by equals: (E, E) -> Bool
) -> [E]
where E: Hashable
{
  // > Duplicates are removed from the the second list
  var s2 = OrderedSet(c2)
  // > elements of the first list are removed from the the second list
  c1.uniqued().forEach { s2.remove($0) }
  // > if the first list contains duplicates, so will the result
  return Array(c1) + Array(s2)
}

/// <https://hackage.haskell.org/package/base-4.17.0.0/docs/Data-List.html#v:-92--92->
func listDifference<E, C1: Collection<E>, C2: Collection<E>>(_ c1: C1, _ c2: C2) -> [E]
where E: Equatable
{
  var res = Array(c1)
  for e in c2 {
    if let index = res.firstIndex(of: e) {
      res.remove(at: index)
    }
  }
  return res
}
