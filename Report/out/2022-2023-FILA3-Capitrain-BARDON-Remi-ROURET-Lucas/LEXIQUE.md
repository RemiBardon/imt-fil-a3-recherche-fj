# Lexique

|Abbréviation|🇬🇧|🇫🇷|
|---|---|---|
| `T` | Type declarations | Déclaration de type |
| `L` and `P` | Classes and interfaces | Classes et interfaces |
| `K` | Constructors | Constructeurs |
| `S` | Signatures | Signatures |
| `M` | Methods | Méthodes |
| `e` | The possible expressions | Les expressions possibles |
| `CT` | A class table `CT` is a mapping from class or interface names, to class or interface declarations, `L` or `P` respectively | Un *mapping* entre les noms des classes ou interfaces et leurs déclarations (instances d'objets Java dans notre cas) |

## Métavariables

|Abbréviation|🇬🇧|🇫🇷|
|---|---|---|
| `A`, `B`, `C`, `D`, and `E` | Class names | Noms de classes |
| `F`, `G`, `H`, `I`, and `J` | Range over interface names | Noms d'interfaces |
| `T`, `U`, and `V` | Generic names for classes or interfaces | Noms génériques de classes et interfaces |
| `f` and `g` | Range over field names | Noms de champs (ex : membres d'une classe) |
| `m` | Range over method names | Noms de méthodes |
| `x` and `y` | Range over variables | Variables |
| `d` and `e` | Range over expressions | Expressions |

## Notations

|Abbréviation|🇬🇧|🇫🇷|
|---|---|---|
| `T̅`, `C̅`, `f̅`, `x̅`… | A possibly empty sequence | Une séquence (≈ liste) possiblement vide |
| `•` | Empty sequence | Séquence (≈ liste) vide |
| `#x̅` | Length of sequence `x̅` | Longueur de la séquence `x̅` |
| `Γ`, `x̅ : T̅` | An environment, which is a finite mapping from variables to types | Un environnement, correspondant à un *mapping* entre les noms des variables et leurs types |
| `T m(T̅ x̅)` | A method named `m`, a return type `T`, and parameters `x̅` of types `T̅` | Une méthode `m` retournant `T` avec des arguments `x̅` (potentiellement aucun) de types `T̅` |
| `(T̅ x̅) → e` | An anonymous function, which has a list of arguments with type `T̅` and names `x̅`, and a body expressions `e` | Une fonction anonyme avec des arguments `x̅` (potentiellement aucun) de types `T̅` et un corps constitué de l'expression `e` |
| `T <: U` | `T` is a subtype of `U` | `T` est un sous-type de `U` (une classe étend une autre classe, une classe implémente une interface, une interface en étend une autre…) |
| `(T̅ x̅)`* | ∅ | Plus généralement, `(T̅ x̅)` représente une liste de paramètres |
| `Γ ⊢ e : ⟨T, e′⟩` | Typing judgment for expressions results in addition to the type a new (possibly annotated) expression | En fonction de l'environnement `Γ` d'une expression, un type `T` est associé à une expression `e′`, corespondant à `e` possiblement annotée elle aussi d'un type |
