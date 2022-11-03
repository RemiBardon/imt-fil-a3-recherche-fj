// Based on <https://github.com/fjpub/fj-lam/blob/cbcb453fb2595d341bb3c1341fdf235f066778fd/FJUtils.hs>.

import func Algorithms.product

/// Checks classes for subtyping.
/// - Parameters: Class table, Class A, Class B.
/// - Returns: Returns if class A is subtype of class B.
func subtyping(ct classTable: CT, _ classA: String, _ classB: String) -> Bool {
  if classA == classB { return true }

  switch classTable[classA] {
  case .some(.class(let classC)):
    if classC.extends == classB || classC.implements.contains(classB) {
      return true
    } else {
      return subtyping(ct: classTable, classC.extends, classB)
        || classC.implements.contains(where: { subtyping(ct: classTable, $0, classB) })
    }

  case .some(.interface(let interface)):
    return interface.extends.contains(classB)
      || interface.extends.contains(where: { subtyping(ct: classTable, $0, classB) })

  case .none:
    return false
  }
}

/// Searches for a class in the class table and returns its fields.
/// - Returns: A monad Maybe containing the field list or Nothing.
func fields(ct classTable: CT, className: String) -> [FJField]? {
  if className == "Object" { return [] }

  switch classTable[className] {
  case .some(.class(let `class`)):
    switch fields(ct: classTable, className: `class`.extends) {
    case .some(let base):
      return base + `class`.fields
    case .none:
      return nil
    }
  default:
    return nil
  }
}


/// Searches for a class or interface in the class table and returns its abstract methods.
/// - Returns: A monad Maybe containing the method signature or Nothin.
func abstractMethods(ct classTable: CT, class className: String) -> [FJSignature]? {
  if className == "Object" { return [] }

  switch classTable[className] {
  case .some(.class(let `class`)):
    switch abstractMethods(ct: classTable, class: `class`.extends) {
    case .some(let bam):
      let bamʹ: [FJSignature] = `class`.implements.flatMap {
        abstractMethods(ct: classTable, class: $0) ?? []
      }
      let bamʺ: [FJSignature] = union(bam, bamʹ) { (s1, s2) in s1.name == s2.name }
      let cam: [FJSignature] = {
        switch methods(ct: classTable, className: `class`.extends) {
        case .some(let meths):
          switch methods(ct: classTable, className: `class`.name) {
          case .some(let bmeths):
            return union(meths.map(\.signature), bmeths.map(\.signature)) { (s1, s2) in
              s1.name == s2.name
            }
          case .none:
            return meths.map(\.signature)
          }
        case .none:
          return []
        }
      }()
      return listDifference(bamʺ, cam)
    case .none:
      return nil
    }
  case .some(.interface(let interface)):
    let bam: [FJSignature] = interface.extends.flatMap {
      abstractMethods(ct: classTable, class: $0) ?? []
    }
    let abstractMethsʹ = union(interface.signatures, bam) { (s1, s2) in
      s1.name == s2.name
    }
    return abstractMethsʹ
  case .none:
    return nil
  }
}


/// Searches for a class in the class table and returns its methods.
/// - Returns: A monad Maybe containing the method list of Nothing.
func methods(ct classTable: CT, className: String) -> [FJMethod]? {
  if className == "Object" { return [] }

  switch classTable[className] {
  case .some(.class(let `class`)):
    switch methods(ct: classTable, className: `class`.extends) {
    case .some(let bms):
      let bim = `class`.implements.flatMap {
        methods(ct: classTable, className: $0) ?? []
      }
      let mʹ = union(`class`.methods, bms) { (m1: FJMethod, m2: FJMethod) in
        m1.signature.name == m2.signature.name
      }
      let mʺ = union(mʹ, bim) { (m1: FJMethod, m2: FJMethod) in
        m1.signature.name == m2.signature.name
      }
      return mʺ
    case .none:
      return nil
    }

  case .some(.interface(let interface)):
    let bim = interface.extends.flatMap {
      methods(ct: classTable, className: $0) ?? []
    }
    let mʹ = union(interface.defaultMethods, bim) { (m1: FJMethod, m2: FJMethod) in
      m1.signature.name == m2.signature.name
    }
    return mʹ

  case .none:
    return nil
  }
}

/// Searches for a class in the class table, then looks up for a method and returns its type.
/// - Returns: A monad Maybe containing the method type.
func methodType(
  ct classTable: CT,
  methodName: String,
  className: String
) -> ([FJTypeName], FJTypeName)? {
  if className == "Object" { return nil }

  guard let absMeths = abstractMethods(ct: classTable, class: className) else { return nil }
  if let signature = absMeths.first(where: { $0.name == methodName }) {
    return (signature.args.map(\.type), signature.typeName)
  }

  guard let meths = methods(ct: classTable, className: className) else { return nil }
  if let signature = meths.map(\.signature).first(where: { $0.name == methodName }) {
    return (signature.args.map(\.type), signature.typeName)
  }

  return nil
}

/// Searches for a class in the class table, then looks up for a method and returns its body.
/// - Returns: A monad Maybe containing the method body or Nothing.
func methodBody(
  ct classTable: CT,
  methodName: String,
  className: String
) -> ([String], FJExpr)? {
  if className == "Object" { return nil }

  guard let meths = methods(ct: classTable, className: className) else { return nil }
  if let method = meths.first(where: { $0.signature.name == methodName }) {
    return (method.signature.args.map(\.name), method.body)
  }

  return nil
}

/// Checks if an expression represents a value.
/// - Returns: Boolean indicating if an expression is a value.
func isValue(ct classTable: CT, _ expression: FJExpr) -> Bool {
  switch expression {
  case .createObject(_, []): return true
  case .createObject(_, let p): return p.allSatisfy { isValue(ct: classTable, $0) }
  case .lambda: return true
  case .cast(_, .lambda): return true
  default: return false
  }
}

/// Annotates the types for lambda expressions.
/// - Returns: A lambda expression annotated with its type, or the expression if
///            it is not a lambda expression.
func lambdaMark(expression: FJExpr, type: FJTypeName) -> FJExpr {
  switch expression {
  case .lambda:
    return .cast(type, expression)
  default:
    return expression
  }
}

/// Removes runtime annotations from lambda expressions.
/// - Returns: An expression without the runtime type annotations.
func removeRuntimeAnnotation(expression: FJExpr) -> FJExpr {
  switch expression {
  case let .fieldAccess(expr, fieldName):
    return .fieldAccess(removeRuntimeAnnotation(expression: expr), fieldName)
  case let .methodInvocation(expr, methodName, parameters):
    return .methodInvocation(
      removeRuntimeAnnotation(expression: expr),
      methodName,
      parameters.map(removeRuntimeAnnotation)
    )
  case let .createObject(className, parameters):
    return .createObject(className, parameters.map(removeRuntimeAnnotation))
  case let .lambda(parameters, expr):
    return .lambda(parameters, removeRuntimeAnnotation(expression: expr))
  case let .cast(typeName, expr):
    return .cast(typeName, removeRuntimeAnnotation(expression: expr))
  default:
    return expression
  }
}
