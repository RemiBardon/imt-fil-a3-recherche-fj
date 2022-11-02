// Based on <https://github.com/fjpub/fj-lam/blob/cbcb453fb2595d341bb3c1341fdf235f066778fd/FJParser.hs>.

// MARK: Syntactic constructors

/// Type definition.
///
/// `T ::= C | I`
enum FJType: Equatable {
  case `class`(FJClass)
  case interface(FJInterface)
}

/// Class declaration.
///
/// `class C extends D implements I̅ { T̅ f̅; K M̅ }`
struct FJClass: Equatable {
  /// Class name (`class C`).
  let name: String
  /// Extended class (`extends D`).
  let extends: String
  /// List of implemented interfaces (`implements I̅`).
  let implements: [String]
  /// List of fields (`T̅ f̅`).
  let fields: [FJField]
  /// Object constructor (`K`).
  let constructor: FJConstructor
  /// List of methods (`M̅`).
  let methods: [FJMethod]
}

/// Interface declaration.
///
/// `P ::= interface I extends I̅ { S̅; default M̅ }`
struct FJInterface: Equatable {
  let name: String
  let extends: [String]
  let signatures: [FJSignature]
  let defaultMethods: [FJMethod]
}
typealias P = FJInterface

/// Constructor declaration.
///
/// `K ::= C(T̅ f̅) { super(f̅); this.f̅ = f̅; }`
struct FJConstructor: Equatable {
  let name: String
  let args: [FJField]
  let superArgs: [String]
  let fieldInits: [FieldInit]
}
typealias K = FJConstructor

/// (Method) signature declaration.
///
/// `S ::= T m(T̅ x̅)`
struct FJSignature: Equatable {
  let typeName: FJTypeName
  let name: String
  let args: [FJField]
}
typealias S = FJSignature

/// Method declaration.
///
/// `M ::= S { return e; }`
///
/// Example:
/// ```java
/// void main(String[] args) {
///   System.out.println("Hello");
/// }
/// ```
struct FJMethod: Equatable {
  let signature: FJSignature
  let body: FJExpr
}
typealias M = FJMethod

/// Expressions.
///
/// ```
/// e ::=
///   x           variable
///   e.f         field access
///   e.m(e̅)      method invocation
///   new C(e̅)    object creation
///   (T)e        cast
///   (T̅ x̅) → e   λ-expression
/// ```
indirect enum FJExpr: Equatable {
  /// Variable (`x`).
  case variable(String)
  /// Field access (`e.f`).
  case fieldAccess(FJExpr, String)
  /// Method invocation (`e.m(e̅)`).
  case methodInvocation(FJExpr, String, [FJExpr])
  /// Object instantiation (`new C(e̅)`).
  case createObject(String, [FJExpr])
  /// Cast (`(T)e`).
  case cast(String, FJExpr)
  /// λ-expression (`(T̅ x̅) → e`).
  case lambda([FJField], FJExpr)
}
typealias e = FJExpr

// MARK: Nominal typing

// Type name.
typealias FJTypeName = String

// MARK: Auxiliary definitions

typealias Env = [String: FJTypeName]
/// Class table.
typealias CT = [FJTypeName: FJType]

// MARK: Typing errors

enum TypeError: Error, Equatable {
  case variableNotFound(String)
  case fieldNotFound(String)
  case classNotFound(String)
  case methodNotFound(String, String)
  case paramsTypeMismatch([TypeMismatch])
  case wrongClosure(String, FJExpr)
  case wrongCast(String, FJExpr)
  case unknownError(FJExpr)
}

// MARK: Swift requirements

// Swift does not automatically synthesize `Equatable` conformance for tuples,
// even when members are `Equatable`. We must define `struct`s to replace pairs.

/// Equivalent of `(FJTypeName, String)`.
struct FJField: Equatable {
  let type: FJTypeName
  let name: String
}

/// Equivalent of `(String, String)` (in `this.f̅ = f̅`).
struct FieldInit: Equatable {
  let fieldName: String
  let argumentName: String
}

/// Equivalent of `(FJExpr, FJTypeName)`.
struct TypeMismatch: Equatable {
  let expression: FJExpr
  let expectedTypeName: FJTypeName
}
