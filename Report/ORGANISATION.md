# Organisation du projet

Le projet possède 2 packages mère :
- `main` ayant la structure de FJ.
- `test` avant nos tests.

En effet, pour tester le projet, nous avons décidé d'utiliser Junit5.

## Package main

Voici la structure du package main :

- model
  - error
  - java
     - expression
	 - misc
	 - type
  - misc
- util.haskell

Description des packages :

> `util.haskell`: Posséde une classe "Haskell" qui permet de faire des opérations sur les listes que Java ne posséde pas.

> `error`: Posséde les classes d'erreurs tel que "VariableNotFound" ou "TypeError".

> `java.expression`: Posséde les classes relatives aux expressions tel que "FJCast" ou "FJLambda".

> `java.type`: Posséde les classes relatives aux types tel que "FJClass" ou "FJType".

> `java.misc`: Posséde les classes relatives aux autres éléments tel que "FJSignature" ou "FJConstructor".

Dans l'UML, vous pouvez voir dans quelle package se trouve chaque classe.

## Package test

Dans le package test, nous avons la class `TypeCheckerTest` qui permet de tester le type checker des approches 1 et 2.<br/>
Nous avons aussi `EvaluationTests` qui permet de tester la réduction que n'avons pas eu le temps de tester.<br/>
Puis, nous avons `FJTest` qui permet de tester l'exemple donné dans le document de recherche.

## Comment lancer les tests

Comme mentionner plus haut, nous avons utilisé Junit5 pour effectuer nos tests unitaires.<br/> Pour lancer les tests, il suffit de lancer la classe `FJTest` et `TypeCheckerTest` qui se trouve dans le package `test`. Pour ça, nous avons préconfiguré une configuration pour Eclipse. <br/>Pour la retrouver il faut aller dans Run > Run Configurations > JUnit vous devriez voir une configuration nommée "TypeCheckTests". Il suffit simplement de lancer la configuration pour lancer les tests de tous nos tests.

## Stratégie de test

Nous avons décidé de tester chaque séquent pour vérifier le bon fonctionnement de l'implémentation. À voir dans la partie "Points forts de notre projet"
