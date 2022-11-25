# Un choix discuté

## Option 1 : Faire référence aux types par des `String`

Comme dans le code Haskell rédigé par les auteurs du papier étudié,
nous avons utilisé le concept de "*class table*". Selon le document,

> *a class table CT is a mapping from class or interface names, to class or interface declarations*

Dans notre code Java, cela correspond à `Map<String, FJType>`.
Nous avons pris la liberté de renommer ce concept en *type table* car on ne stocke pas que des classes,
mais aussi des interfaces et potentiellement des types "fonction" (ou autre si évolutions du langage).

Le *parser* FJ (non implémenté dans notre cas) lit du code Java, donc cela a du sens de stocker des `String`.
Cependant, cela rend plus complexe le code (entendre complexité algorithmique)
car il faut toujours aller chercher dans la *type table* pour récupérer les instances des objets.

Nous avons donc besoin d'écrire

```java
final String expectedReturnTypeName = typedBody.typeName();
final boolean returnTypeIsCorrect = context.typeTable.isSubtype(expectedReturnTypeName, this.signature.returnTypeName());
if (!returnTypeIsCorrect) {
    // …
}
```

là où

```java
if (!typedBody.type().isSubtype(this.signature.returnType)) {
    // …
}
```

pourrait suffir si on stackait des `FJType` directement.

## Option 2 : Faire référence aux types par des objets

Nous avions comme tâche d'écrire le code "le plus objet possible",
alors nous avons rapidement pensé à faire référence aux types par des objets.
Cela nous aurait permis de faire disparaître la *type table*,
qui est un point central source de beaucoup d'erreurs dans l'autre cas.

La plupart des `Exception`s que l'on utilise
(notamment `ClassNotFound` quand une classe n'est pas trouvée dans la *type table*)
pourraient disparaître et ainsi simplifier grandement le code.
Cela nous permettrait par exemple d'utiliser des `Stream` que nous avons du remplacer par des "for each"
car `Stream::map` ne permet pas de passer une méthode/lambda qui lève des `Exception`s.
Ces erreurs nous empêchent d'écrire

```java
return typeTable.isSubtype(this.extendsName, otherTypeName)
    || this.implementsNames.stream().anyMatch(t -> typeTable.isSubtype(t, otherTypeName));
```

et nous forcent à écrire

```java
if(typeTable.isSubtype(this.extendsName, otherTypeName)){
    return true;
}
boolean isSubType = true;
for (String implementsName : this.implementsNames) {
    isSubType &= !typeTable.isSubtype(implementsName, otherTypeName);
}
return isSubType;
```

Ce n'est pas très important, mais cela impacte la lisibilité du code.

Le principal inconvénient de cette option est qu'elle complexifie grandement la création des objets.
Pour résoudre ce problème, nous avons imaginé une solution expliquée ci-dessous.

## Notre choix

> explication de pourquoi l'option 1 a été choisie plutôt que l'option 2

Nous avons choisi d'utiliser une *type table* et de stocker des `String`
car c'est comme ça que fonctionne le code original en Haskell,
mais nous avions prévu (si on avait eu plus de temps) d'implémenter l'option 2 à terme.

Lors de ce changement d'implémentation interne à la librairie,
il était important pour nous de ne pas "casser" les tests.
Garder les tests fonctionnels permet de vérifier que l'on n'intère pas de régression,
ce qui est crucial pendant un *refactoring*.
Pour faciliter l'implémentation, nous avons donc créé des *builders* qui font l'interface
entre les tests rédigés avec des `String`, et la librairie qui ensuite fonctionnerait avec des objets.
