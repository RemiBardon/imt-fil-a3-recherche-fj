// Based on <https://github.com/fjpub/fj-lam/blob/cbcb453fb2595d341bb3c1341fdf235f066778fd/FJUtils.hs>.

import func Algorithms.product

/// Checks is a class is a subtype of another class.
/// - Returns: Returns if `classA` is a subtype of `classB`.
func isSubtype(classTable: ClassTable, _ classA: FJClassName, _ classB: FJClassName) -> Bool {
  if classA == classB { return true }

  switch classTable[classA] {
  case .some(.class(let classC)):
    if classC.extends == classB || classC.implements.contains(classB) {
      return true
    } else {
      return isSubtype(classTable: classTable, classC.extends, classB)
        || classC.implements.contains(where: { isSubtype(classTable: classTable, $0, classB) })
    }

  case .some(.interface(let interface)):
    return interface.extends.contains(classB)
      || interface.extends.contains(where: { isSubtype(classTable: classTable, $0, classB) })

  case .none:
    return false
  }
}

/// Searches for a class in the class table and returns its fields.
/// - Returns: The list of fields or nothing (`nil`) if the class was not found.
func classFields(classTable: ClassTable, className: FJTypeName) -> [FJField]? {
  if className == "Object" { return [] }

  switch classTable[className] {
  case .some(.class(let `class`)):
    return classFields(classTable: classTable, className: `class`.extends)
      .map { $0 + `class`.fields }
  default:
    return nil
  }
}


/// Searches for a class or interface in the class table and returns its abstract methods.
/// - Returns: The abstract methods signatures or nothing (`nil`) if the type was not found.
func abstractMethods(classTable: ClassTable, typeName: FJTypeName) -> [FJSignature]? {
  if typeName == "Object" { return [] }

  switch classTable[typeName] {
  case .some(.class(let `class`)):
    return abstractMethods(
      classTable: classTable,
      typeName: `class`.extends
    ).map { superAbstractMethods in
      let implementsAbstractMethods: [FJSignature] = `class`.implements.flatMap {
        FJ.abstractMethods(classTable: classTable, typeName: $0) ?? []
      }
      let abstractMethods = union(superAbstractMethods, implementsAbstractMethods) { (s1, s2) in
        s1.name == s2.name
      }
      let concreteMethods: [FJSignature] = {
        guard let superMethods = methods(classTable: classTable, className: `class`.extends) else {
          return []
        }
        if let methods = methods(classTable: classTable, className: `class`.name) {
          return union(superMethods.map(\.signature), methods.map(\.signature)) { (s1, s2) in
            s1.name == s2.name
          }
        } else {
          return superMethods.map(\.signature)
        }
      }()
      return listDifference(abstractMethods, concreteMethods)
    }
  case .some(.interface(let interface)):
    let superAbstractMethods: [FJSignature] = interface.extends.flatMap {
      FJ.abstractMethods(classTable: classTable, typeName: $0) ?? []
    }
    let abstractMethods = union(interface.signatures, superAbstractMethods) { (s1, s2) in
      s1.name == s2.name
    }
    return abstractMethods
  case .none:
    return nil
  }
}


/// Searches for a class in the class table and returns its methods.
/// - Returns: A monad Maybe containing the method list of Nothing.
func methods(classTable: ClassTable, className: FJTypeName) -> [FJMethod]? {
  if className == "Object" { return [] }

  switch classTable[className] {
  case .some(.class(let `class`)):
    switch methods(classTable: classTable, className: `class`.extends) {
    case .some(let bms):
      let bim = `class`.implements.flatMap {
        methods(classTable: classTable, className: $0) ?? []
      }
      let m?? = union(`class`.methods, bms) { (m1: FJMethod, m2: FJMethod) in
        m1.signature.name == m2.signature.name
      }
      let m?? = union(m??, bim) { (m1: FJMethod, m2: FJMethod) in
        m1.signature.name == m2.signature.name
      }
      return m??
    case .none:
      return nil
    }

  case .some(.interface(let interface)):
    let bim = interface.extends.flatMap {
      methods(classTable: classTable, className: $0) ?? []
    }
    let m?? = union(interface.defaultMethods, bim) { (m1: FJMethod, m2: FJMethod) in
      m1.signature.name == m2.signature.name
    }
    return m??

  case .none:
    return nil
  }
}

/// Searches for a class in the class table, then looks up for a method and returns its type.
/// - Returns: A monad Maybe containing the method type.
func methodType(
  classTable: ClassTable,
  methodName: String,
  className: FJTypeName
) -> ([FJTypeName], FJTypeName)? {
  if className == "Object" { return nil }

  guard let absMeths = abstractMethods(
    classTable: classTable,
    typeName: className
  ) else { return nil }
  if let signature = absMeths.first(where: { $0.name == methodName }) {
    return (signature.args.map(\.type), signature.returnTypeName)
  }

  guard let meths = methods(classTable: classTable, className: className) else { return nil }
  if let signature = meths.map(\.signature).first(where: { $0.name == methodName }) {
    return (signature.args.map(\.type), signature.returnTypeName)
  }

  return nil
}

/// Searches for a class in the class table, then looks up for a method and returns its body.
/// - Returns: A monad Maybe containing the method body or Nothing.
func methodBody(
  classTable: ClassTable,
  methodName: String,
  className: FJTypeName
) -> ([String], FJExpr)? {
  if className == "Object" { return nil }

  guard let meths = methods(classTable: classTable, className: className) else { return nil }
  if let method = meths.first(where: { $0.signature.name == methodName }) {
    return (method.signature.args.map(\.name), method.body)
  }

  return nil
}

/// Checks if an expression represents a value.
/// - Returns: Boolean indicating if an expression is a value.
func isValue(_ expression: FJExpr) -> Bool {
  switch expression {
  case .createObject(_, []): return true
  case .createObject(_, let p): return p.allSatisfy(isValue(_:))
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
    return .cast(typeName: type, expression: expression)
  default:
    return expression
  }
}

/// Removes runtime annotations from lambda expressions.
/// - Returns: An expression without the runtime type annotations.
func removeRuntimeAnnotation(expression: FJExpr) -> FJExpr {
  switch expression {
  case let .fieldAccess(expr, fieldName):
    return .fieldAccess(
      source: removeRuntimeAnnotation(expression: expr),
      fieldName: fieldName
    )
  case let .methodInvocation(expr, methodName, parameters):
    return .methodInvocation(
      source: removeRuntimeAnnotation(expression: expr),
      methodName: methodName,
      parameters: parameters.map(removeRuntimeAnnotation)
    )
  case let .createObject(className, parameters):
    return .createObject(
      className: className,
      arguments: parameters.map(removeRuntimeAnnotation)
    )
  case let .lambda(parameters, expr):
    return .lambda(parameters: parameters, body: removeRuntimeAnnotation(expression: expr))
  case let .cast(typeName, expression):
    return .cast(typeName: typeName, expression: removeRuntimeAnnotation(expression: expression))
  default:
    return expression
  }
}
