// Based on <https://github.com/fjpub/fj-lam/blob/cbcb453fb2595d341bb3c1341fdf235f066778fd/FJParser.hs>.

// MARK: Syntactic constructors

/// Type definition.
///
/// `T ::= C | I`
public enum FJType: Hashable {
  case `class`(FJClass)
  case interface(FJInterface)
}

/// Class declaration.
///
/// `class C extends D implements I̅ { T̅ f̅; K M̅ }`
public struct FJClass: Hashable {
  /// Class name (`class C`).
  public let name: String
  /// Extended class (`extends D`).
  public let extends: String
  /// List of implemented interfaces (`implements I̅`).
  public let implements: [String]
  /// List of fields (`T̅ f̅`).
  public let fields: [FJField]
  /// Object constructor (`K`).
  public let constructor: FJConstructor
  /// List of methods (`M̅`).
  public let methods: [FJMethod]

  public init(
    name: String,
    extends: String = "Object",
    implements: [String] = [],
    fields: [FJField] = [],
    constructor: FJConstructor,
    methods: [FJMethod] = []
  ) {
    self.name = name
    self.extends = extends
    self.implements = implements
    self.fields = fields
    self.constructor = constructor
    self.methods = methods
  }
}

/// Interface declaration.
///
/// `P ::= interface I extends I̅ { S̅; default M̅ }`
public struct FJInterface: Hashable {
  public let name: String
  public let extends: [String]
  public let signatures: [FJSignature]
  public let defaultMethods: [FJMethod]

  public init(
    name: String,
    extends: [String] = [],
    signatures: [FJSignature] = [],
    defaultMethods: [FJMethod] = []
  ) {
    self.name = name
    self.extends = extends
    self.signatures = signatures
    self.defaultMethods = defaultMethods
  }
}
typealias P = FJInterface

/// Constructor declaration.
///
/// `K ::= C(T̅ f̅) { super(f̅); this.f̅ = f̅; }`
public struct FJConstructor: Hashable {
  public let name: String
  public let args: [FJField]
  public let superArgs: [String]
  public let fieldInits: [FieldInit]

  public init(
    name: String,
    args: [FJField] = [],
    superArgs: [String] = [],
    fieldInits: [FieldInit] = []
  ) {
    self.name = name
    self.args = args
    self.superArgs = superArgs
    self.fieldInits = fieldInits
  }
}
typealias K = FJConstructor

/// (Method) signature declaration.
///
/// `S ::= T m(T̅ x̅)`
public struct FJSignature: Hashable {
  public let typeName: FJTypeName
  public let name: String
  public let args: [FJField]

  public init(
    typeName: FJTypeName,
    name: String,
    args: [FJField] = []
  ) {
    self.typeName = typeName
    self.name = name
    self.args = args
  }
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
public struct FJMethod: Hashable {
  public let signature: FJSignature
  public let body: FJExpr

  public init(
    signature: FJSignature,
    body: FJExpr
  ) {
    self.signature = signature
    self.body = body
  }
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
public indirect enum FJExpr: Hashable {
  /// Variable (`x`).
  case variable(String)
  /// Field access (`e.f`).
  case fieldAccess(FJExpr, String)
  /// Method invocation (`e.m(e̅)`).
  case methodInvocation(FJExpr, String, [FJExpr])
  /// Object instantiation (`new C(e̅)`).
  case createObject(String, [FJExpr])
  /// Cast (`(T)e`).
  case cast(FJTypeName, FJExpr)
  /// λ-expression (`(T̅ x̅) → e`).
  case lambda([FJField], FJExpr)
}
typealias e = FJExpr

// MARK: Nominal typing

/// Type name.
public typealias FJTypeName = String

// MARK: Auxiliary definitions

public typealias Context = [String: FJTypeName]
/// Class table.
public typealias CT = [FJTypeName: FJType]

// MARK: Typing errors

public enum TypeError: Error, Hashable {
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

// Swift does not automatically synthesize `Hashable` conformance for tuples,
// even when members are `Hashable`. We must define `struct`s to replace pairs.

/// Equivalent of `(FJTypeName, String)`.
public struct FJField: Hashable {
  public let type: FJTypeName
  public let name: String

  public init(
    type: FJTypeName,
    name: String
  ) {
    self.type = type
    self.name = name
  }
}

/// Equivalent of `(String, String)` (in `this.f̅ = f̅`).
public struct FieldInit: Hashable {
  public let fieldName: String
  public let argumentName: String

  public init(
    fieldName: String,
    argumentName: String
  ) {
    self.fieldName = fieldName
    self.argumentName = argumentName
  }
}

/// Equivalent of `(FJExpr, FJTypeName)`.
public struct TypeMismatch: Hashable {
  public let expression: FJExpr
  public let expectedTypeName: FJTypeName

  public init(
    expression: FJExpr,
    expectedTypeName: FJTypeName
  ) {
    self.expression = expression
    self.expectedTypeName = expectedTypeName
  }
}
