// Based on <https://github.com/fjpub/fj-lam/blob/cbcb453fb2595d341bb3c1341fdf235f066778fd/V2/FJTypeChecker.hs>.

/// Checks the type of a given expression.
/// - Returns: The type of a given term or a type error.
func typeNameOf(
  _ expression: FJExpr,
  classTable: ClassTable,
  context: Context
) -> Result<FJTypeName, TypeError> {
  switch expression {
  case .variable(let varName): // T-Var
    if let typeName = context[varName] {
      return .success(typeName)
    } else {
      return .failure(.variableNotFound(name: varName))
    }

  case let .fieldAccess(e, f): // T-Field
    return typeNameOf(e, classTable: classTable, context: context).flatMap { typeName in
      guard let fields = classFields(classTable: classTable, className: typeName) else {
        return .failure(.classNotFound(name: typeName))
      }
      if let field = fields.first(where: { $0.name == f }) {
        return .success(field.type)
      } else {
        return .failure(.fieldNotFound(name: f))
      }
    }

  case let .methodInvocation(e, methodName, parameters): // T-Invk
    return typeNameOf(e, classTable: classTable, context: context).flatMap { typeName in
      guard let (parameterTypes, returnType) = methodType(
        classTable: classTable,
        methodName: methodName,
        className: typeName
      ) else {
        return .failure(.methodNotFound(name: methodName, returnTypeName: typeName))
      }
      guard parameters.count == parameterTypes.count else {
        return .failure(.paramsTypeMismatch(params: []))
      }

      let tmp = zip(parameters, parameterTypes).map { (e, t) in
        TypeMismatch(expression: lambdaMark(expression: e, type: t), expectedTypeName: t)
      }
      let isCorrectlyTyped = tmp.allSatisfy {
        switch typeNameOf($0.expression, classTable: classTable, context: context) {
        case .success(let t):
          return isSubtype(classTable: classTable, t, $0.expectedTypeName)
        case .failure:
          return false
        }
      }

      if isCorrectlyTyped {
        return .success(returnType)
      } else {
        return .failure(.paramsTypeMismatch(params: tmp))
      }
    }

  case let .createObject(className, parameters): // T-New
    guard let fields = classFields(classTable: classTable, className: className) else {
      return .failure(.classNotFound(name: className))
    }
    guard parameters.count == fields.count else {
      return .failure(.paramsTypeMismatch(params: []))
    }

    let tmp = zip(parameters, fields).map { (e, f) in
      TypeMismatch(expression: lambdaMark(expression: e, type: f.type), expectedTypeName: f.type)
    }
    let isCorrectlyTyped = tmp.allSatisfy {
      switch typeNameOf($0.expression, classTable: classTable, context: context) {
      case .success(let t):
        return isSubtype(classTable: classTable, t, $0.expectedTypeName)
      case .failure:
        return false
      }
    }

    if isCorrectlyTyped {
      return .success(className)
    } else {
      return .failure(.paramsTypeMismatch(params: tmp))
    }

  case let .cast(type, castExpr):
    switch castExpr {
    case let .lambda(parameters, lambdaExpr): // T-Lam
      let context = context.merging(
        parameters.map { ($0.name, $0.type) },
        uniquingKeysWith: { a, _ in a }
      )
      guard let abstractMethods = abstractMethods(classTable: classTable, typeName: type),
            abstractMethods.count == 1,
            let method = abstractMethods.first
      else { return .failure(.wrongLambdaType(targetTypeName: type, lambda: castExpr)) }

      return typeNameOf(
        lambdaMark(expression: lambdaExpr, type: method.returnTypeName),
        classTable: classTable,
        context: context
      ).flatMap { expectedType in
        if isSubtype(classTable: classTable, expectedType, method.returnTypeName)
            && method.args.first == parameters.first {
          return .success(type)
        } else {
          return .failure(.wrongLambdaType(targetTypeName: type, lambda: castExpr))
        }
      }

    default:
      let castExprʹ = lambdaMark(expression: castExpr, type: type)
      return typeNameOf(
        castExprʹ,
        classTable: classTable,
        context: context
      ).flatMap { expectedType in
        let expectedTypeIsType = isSubtype(classTable: classTable, expectedType, type)
        let typeIsExpectedType = isSubtype(classTable: classTable, type, expectedType)

        if (expectedTypeIsType) // T-UCast
        || (typeIsExpectedType && type != expectedType) // T-DCast
        || (!typeIsExpectedType && !expectedTypeIsType) // T-SCast
        {
          return .success(type)
        } else {
          return .failure(.wrongCast(castType: type, expression: castExpr))
        }
      }
    }

  case .lambda: // Error: Lambda expression without a type
    return .failure(.wrongLambdaType(targetTypeName: "None", lambda: expression))
  }
}

/// Checks if a method is well formed.
/// - Returns: `true` for a well formed method, `false` otherwise.
public func methodTyping(
  classTable: ClassTable,
  context: Context,
  className: FJTypeName,
  method: FJMethod
) -> Bool {
  let eʹ = lambdaMark(expression: method.body, type: method.signature.returnTypeName)
  let contextualArguments = method.signature.args.map { ($0.name, $0.type) } + [("this", className)]
  let context = context.merging(contextualArguments, uniquingKeysWith: { a, _ in a })
  switch typeNameOf(eʹ, classTable: classTable, context: context) {
  case .success(let exprType):
    if let meths = methods(classTable: classTable, className: exprType) {
      return isSubtype(classTable: classTable, exprType, method.signature.returnTypeName)
        && meths.contains(method)
    } else {
      return false // Error obtaining methods
    }
  case .failure:
    return false // Error obtaining type of expression
  }

}

/// Checks if a class is well-formed.
/// - Returns: `true` for a well-formed class, `false` otherwise.
public func classTyping(classTable: ClassTable, context: Context, class: FJClass) -> Bool {
  guard let fields = classFields(classTable: classTable, className: `class`.extends) else {
    return false
  }
  guard `class`.constructor.args == fields + `class`.fields else {
    return false
  }
  guard `class`.constructor.fieldInits.allSatisfy({ $0.fieldName == $0.argumentName }) else {
    return false
  }
  guard let absMeths = abstractMethods(classTable: classTable, typeName: `class`.name) else {
    return false // Error obtaining abstract methods
  }

  let pʹ = `class`.constructor.args.map(\.name)
  let pʺ = `class`.constructor.superArgs + `class`.constructor.fieldInits.map(\.fieldName)

  return absMeths.isEmpty
    && (pʹ == pʺ)
    && `class`.methods.allSatisfy {
      methodTyping(classTable: classTable, context: context, className: `class`.name, method: $0)
    }
}

/// Checks if an interface is well-formed.
/// - Returns: `true` for a well-formed interface, `false` otherwise.
public func interfaceTyping(classTable: ClassTable, context: Context, interface: FJInterface) -> Bool {
  return abstractMethods(classTable: classTable, typeName: interface.name) != nil
    && interface.defaultMethods.allSatisfy {
      methodTyping(classTable: classTable, context: context, className: interface.name, method: $0)
    }
}
