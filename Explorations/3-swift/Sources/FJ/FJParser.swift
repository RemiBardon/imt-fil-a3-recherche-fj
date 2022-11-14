// Based on <https://github.com/fjpub/fj-lam/blob/cbcb453fb2595d341bb3c1341fdf235f066778fd/FJParser.hs>.

// MARK: Syntactic constructors

/// Type definition.
///
/// `T ::= C | I`
public enum FJType: Hashable {
  case `class`(_ class: FJClass)
  case interface(_ interface: FJInterface)
}
typealias T = FJType

/// Class declaration.
///
/// `class C extends D implements I̅ { T̅ f̅; K M̅ }`
public struct FJClass: Hashable {
  /// Class name (`class C`).
  public let name: FJClassName
  /// Extended class (`extends D`).
  public let extends: FJClassName
  /// List of implemented interfaces (`implements I̅`).
  public let implements: [FJInterfaceName]
  /// List of fields (`T̅ f̅`).
  public let fields: [FJField]
  /// Object constructor (`K`).
  public let constructor: FJConstructor
  /// List of methods (`M̅`).
  public let methods: [FJMethod]

  public init(
    name: FJClassName,
    extends: FJClassName = "Object",
    implements: [FJInterfaceName] = [],
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
  public let name: FJInterfaceName
  public let extends: [FJInterfaceName]
  public let signatures: [FJSignature]
  public let defaultMethods: [FJMethod]

  public init(
    name: FJInterfaceName,
    extends: [FJInterfaceName] = [],
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
  public let name: FJClassName
  public let args: [FJField]
  public let superArgs: [FJVariableName]
  public let fieldInits: [FieldInit]

  public init(
    name: FJClassName,
    args: [FJField] = [],
    superArgs: [FJVariableName] = [],
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
  public let returnTypeName: FJTypeName
  public let name: FJMethodName
  public let args: [FJField]

  public init(
    returnTypeName: FJTypeName,
    name: FJMethodName,
    args: [FJField] = []
  ) {
    self.returnTypeName = returnTypeName
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
  case variable(name: FJVariableName)
  /// Field access (`e.f`).
  case fieldAccess(source: FJExpr, fieldName: FJVariableName)
  /// Method invocation (`e.m(e̅)`).
  case methodInvocation(source: FJExpr, methodName: FJMethodName, parameters: [FJExpr])
  /// Object instantiation (`new C(e̅)`).
  case createObject(className: FJClassName, arguments: [FJExpr])
  /// Cast (`(T)e`).
  case cast(typeName: FJTypeName, expression: FJExpr)
  /// λ-expression (`(T̅ x̅) → e`).
  case lambda(parameters: [FJField], body: FJExpr)
}
typealias e = FJExpr

// MARK: Nominal typing

/// Type name.
public typealias FJTypeName = String
/// Class name.
public typealias FJClassName = String
/// Interface name.
public typealias FJInterfaceName = String
/// Variable name.
public typealias FJVariableName = String
/// Method name.
public typealias FJMethodName = String

// MARK: Auxiliary definitions

public typealias Context = [FJVariableName: FJTypeName]
/// Class table.
public typealias ClassTable = [FJTypeName: FJType]
typealias CT = ClassTable

// MARK: Typing errors

public enum TypeError: Error, Hashable {
  case variableNotFound(name: FJVariableName)
  case fieldNotFound(name: FJVariableName)
  case classNotFound(name: FJClassName)
  case methodNotFound(name: FJMethodName, returnTypeName: FJTypeName)
  case paramsTypeMismatch(params: [TypeMismatch])
  case wrongLambdaType(targetTypeName: FJTypeName, lambda: FJExpr)
  case wrongCast(castType: FJTypeName, expression: FJExpr)
//  case unknownError(expression: FJExpr)
}

// MARK: Swift requirements

// Swift does not automatically synthesize `Hashable` conformance for tuples,
// even when members are `Hashable`. We must define `struct`s to replace pairs.

/// Equivalent of `(FJTypeName, FJVariableName)`.
public struct FJField: Hashable {
  public let type: FJTypeName
  public let name: FJVariableName

  public init(
    type: FJTypeName,
    name: FJVariableName
  ) {
    self.type = type
    self.name = name
  }
}

/// Equivalent of `(FJVariableName, FJVariableName)` (in `this.f̅ = f̅`).
public struct FieldInit: Hashable {
  public let fieldName: FJVariableName
  public let argumentName: FJVariableName

  public init(
    fieldName: FJVariableName,
    argumentName: FJVariableName
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
