# Points forts de notre projet

# Code
## Bien structuré
Comme mentionné dans la partie organisation, nous avons fait un effort sur la structuration du projet, qu'on peut voir dans l'UML.

## Lisible
Le code est lisible et commenté. Nous avons fait un effort sur la lisibilité du code. Utiliser des interfaces par exemple pour simplifier la compréhension du code et de son développement

## Très orienté objet
Il est simple de vérifier et de comprendre les liens entre les classes. Le type checker et l'évaluation est adapté à l'objet.

## Variables et noms de fonctions clairs
Les variables et les noms de fonctions sont clairs et explicites.


# Tests
## Vraie librairie de tests (pas simplement un `main`)

Pour **simplifier la création des objets**, nous avons utilisé les builders. Cela nous a permis de créer des objets complexes sans avoir à passer par un constructeur. Cela nous a permis de simplifier la création des objets complexes.

Voici un exemple:

```java
/*
Eq: 
class C extends Objects extends B {
	public C() {super();}
}
*/
FJClass classC = new FJClassBuilder()
	.name("C")
	.constructor()
	.extendsName("B")
	.build();
```

