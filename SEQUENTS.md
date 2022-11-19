# Séquents

## Séquents globaux

> **Note**
> Ces séquents sont implémentés dans `FJUtils.hs`.

### Subtyping relation between classes and interfaces

- ```logic
  T <: T
  ```

  `T` est un sous-type de `T` (toujours vrai).

- ```logic
  T <: U   U <: V
  ───────────────
      T <: V
  ```

  Si `T` est un sous-type de `U`,
  et que `U` est un sous-type de `V`,
  alors `T` est un sous-type de `V`.

- ```logic
  CT(C) = class C extends D implements I̅ { ... }
  ──────────────────────────────────────────────
             C <: D   C <: I̅
  ```

  Si la *class table* contient une définition de la classe `C` disant qu'elle étend `D` et implémente `I̅`,
  alors `C` est un sous-type de `D`
  et `C` est un sous-type de toutes les interfaces de `I̅`.

- ```logic
  CT(I) = interface I extends I̅ { ... }
  ─────────────────────────────────────
               I <: I̅
  ```

  Si la *class table* contient une définition de l'interface `I` disant qu'elle étend `I̅`,
  alors `I` est un sous-type de toutes les interfaces de `I̅`.

### Abstract method lookup

- ```logic
  abs-methods(Object) = •
  ```

  Si aucun autre séquent n'indique autrement, les méthodes abstraites de tout objet sont vides
  (`!= null` et `!= Optional.empty()`, mais bien une liste avec aucun élément).

  > **Warning**
  > Ce séquent **ne veut pas dire**
  > > L'ensemble des méthodes abstraites d'une classe est égal à `∅`.
  > > ⇔ Les classes n'ont pas de méthodes abstraites.
  > > ⇔ La liste des méthodes abstraites d'une classe est vide (`!= null` et `!= Optional.empty()` mais aucun élément).

- ```logic
  CT(C) = class C extends D implements I̅ { T̅ f̅; K M̅ }
                 M̅ = S̅‾{̅‾r̅e̅t̅u̅r̅n̅‾e̅;̅‾}̅
                 abs-methods(D) = S̅₁̅
                 abs-methods(I̅) = S̅₂̅
  ───────────────────────────────────────────────────
             abs-methods(C) = S̅₁̅ ⊎ S̅₂̅ − S̅
  ```

  - Si la *class table* contient une définition de la classe `C`
    - soit `D` la classe étendue par `C`
    - soit `I̅` les interfaces implémentées par `C`
    - soit `M̅` les méthodes de `C`
    - soit `S̅` les signatures des méthodes `M̅`
  - et que aucune méthode de `M̅` ne contient de *body*
  - soit `S̅₁̅` les signatures des méthodes abstraites de la classe `D`
  - soit `S̅₂̅` les signatures de toutes les méthodes abstraites des interfaces de `I̅`
  - alors les signatures des méthodes abstraites de `C` sont égales à `S̅₁̅` plus `S̅₂̅` moins `S̅`.

- ```logic
  CT(I) = interface I extends I̅ { S̅; default M̅ }
              abs-methods(I̅) = S̅₁̅
  ──────────────────────────────────────────────
            abs-methods(I) = S̅ ⊎ S̅₁̅
  ```

  - Si la *class table* contient une définition de l'interface `I`
    - soit `I̅` les interfaces étendues par `I`
    - soit `S̅` les signatures des méthodes de `I`
    - soit `M̅` les méthodes par défaut de `I`
  - soit `S̅₁̅` les signatures de toutes les méthodes abstraites des interfaces de `I̅`
  - alors les signatures des méthodes abstraites de `I` sont égales à `S̅₁̅` plus `S̅₂̅`.

### Concrete method lookup

TODO: Traduire les séquents.

<!--
- ```logic
  ```

  Si ,
  et ,
  et ,
  alors .

- ```logic
  ```

  Si ,
  et ,
  et ,
  alors .

- ```logic
  ```

  Si ,
  et ,
  et ,
  alors .
-->

### Annotating types for λ-expressions

TODO: Traduire les séquents.

<!--
- ```logic
  ```

  Si ,
  et ,
  et ,
  alors .

- ```logic
  ```

  Si ,
  et ,
  et ,
  alors .
-->

## Approche 1

### T-Var 1

```logic
  Γ ⊢ x : T
────────────── [T-Var]
Γ ⊢ x ↝ ⟨T,x⟩
```

Si l'environnement contient une variable `x` de type `T`,
alors `x` peut être annotée du type `T`.

### T-Field 1

```logic
Γ ⊢ e₀ : ⟨C₀,e′₀⟩   fields(C₀) = T̅ f̅
──────────────────────────────────── [T-Field]
       Γ ⊢ e₀.fᵢ ↝ ⟨Tᵢ,e′₀.fᵢ⟩
```

Si l'environnement contient une expression `e₀` associée à `e′₀` annotée du type `C₀`,
soient `f̅` les membres de la classe `C₀` de types `T̅`,
alors `e₀.fᵢ` peut être annotée du type `Tᵢ` (appliqué à `e′₀.fᵢ`).

## Approche 2
