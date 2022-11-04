// Based on <https://github.com/fjpub/fj-lam/blob/cbcb453fb2595d341bb3c1341fdf235f066778fd/V2/FJTypeChecker.hs>.

/// Checks the type of a given expression.
/// - Returns: The type of a given term or a type error.
func typeof(
  ct classTable: CT,
  context: Context,
  expression: FJExpr
) -> Result<FJTypeName, TypeError> {
  switch expression {
  case .variable(let varName): // T-Var
    if let typeName = context[varName] {
      return .success(typeName)
    } else {
      return .failure(.variableNotFound(varName))
    }

  case let .fieldAccess(e, f): // T-Field
    return typeof(ct: classTable, context: context, expression: e).flatMap { typeName in
      guard let fields = fields(ct: classTable, className: typeName) else {
        return .failure(.classNotFound(typeName))
      }
      if let field = fields.first(where: { $0.name == f }) {
        return .success(field.type)
      } else {
        return .failure(.fieldNotFound(f))
      }
    }

  case let .methodInvocation(e, methodName, parameters): // T-Invk
    return typeof(ct: classTable, context: context, expression: e).flatMap { typeName in
      guard let (parameterTypes, returnType) = methodType(
        ct: classTable,
        methodName: methodName,
        className: typeName
      ) else {
        return .failure(.methodNotFound(methodName, typeName))
      }
      guard parameters.count == parameterTypes.count else {
        return .failure(.paramsTypeMismatch([]))
      }

      let tmp = zip(parameters, parameterTypes).map { (e, t) in
        TypeMismatch(expression: lambdaMark(expression: e, type: t), expectedTypeName: t)
      }
      let isCorrectlyTyped = tmp.allSatisfy {
        switch typeof(ct: classTable, context: context, expression: $0.expression) {
        case .success(let t):
          return subtyping(ct: classTable, t, $0.expectedTypeName)
        case .failure:
          return false
        }
      }

      if isCorrectlyTyped {
        return .success(returnType)
      } else {
        return .failure(.paramsTypeMismatch(tmp))
      }
    }

  case let .createObject(className, parameters): // T-New
    guard let fields = fields(ct: classTable, className: className) else {
      return .failure(.classNotFound(className))
    }
    guard parameters.count == fields.count else {
      return .failure(.paramsTypeMismatch([]))
    }

    let tmp = zip(parameters, fields).map { (e, f) in
      TypeMismatch(expression: lambdaMark(expression: e, type: f.type), expectedTypeName: f.type)
    }
    let isCorrectlyTyped = tmp.allSatisfy {
      switch typeof(ct: classTable, context: context, expression: $0.expression) {
      case .success(let t):
        return subtyping(ct: classTable, t, $0.expectedTypeName)
      case .failure:
        return false
      }
    }

    if isCorrectlyTyped {
      return .success(className)
    } else {
      return .failure(.paramsTypeMismatch(tmp))
    }

  case let .cast(type, castExpr):
    switch castExpr {
    case let .lambda(parameters, lambdaExpr): // T-Lam
      let context = context.merging(
        parameters.map { ($0.name, $0.type) },
        uniquingKeysWith: { a, _ in a }
      )
      guard let abstractMethods = abstractMethods(ct: classTable, class: type),
            abstractMethods.count == 1,
            let method = abstractMethods.first
      else { return .failure(.wrongClosure(type, castExpr)) }

      return typeof(
        ct: classTable,
        context: context,
        expression: lambdaMark(expression: lambdaExpr, type: method.typeName)
      ).flatMap { expectedType in
        if subtyping(ct: classTable, expectedType, method.typeName)
            && method.args.first == parameters.first {
          return .success(type)
        } else {
          return .failure(.wrongClosure(type, castExpr))
        }
      }

    default:
      let castExprʹ = lambdaMark(expression: castExpr, type: type)
      return typeof(
        ct: classTable,
        context: context,
        expression: castExprʹ
      ).flatMap { expectedType in
        let expectedTypeIsType = subtyping(ct: classTable, expectedType, type)
        let typeIsExpectedType = subtyping(ct: classTable, type, expectedType)

        if (expectedTypeIsType) // T-UCast
        || (typeIsExpectedType && type != expectedType) // T-DCast
        || (!typeIsExpectedType && !expectedTypeIsType) // T-SCast
        {
          return .success(type)
        } else {
          return .failure(.wrongCast(type, castExpr))
        }
      }
    }

  case .lambda: // Error: Lambda expression without a type
    return .failure(.wrongClosure("None", expression))
  }
}

/// Checks if a method is well formed.
/// - Returns: `true` for a well formed method, `false` otherwise.
func methodTyping(
  ct classTable: CT,
  context: Context,
  className: FJTypeName,
  method: FJMethod
) -> Bool {
  let eʹ = lambdaMark(expression: method.body, type: method.signature.typeName)
  let contextualArguments = method.signature.args.map { ($0.name, $0.type) } + [("this", className)]
  let context = context.merging(contextualArguments, uniquingKeysWith: { a, _ in a })
  switch typeof(ct: classTable, context: context, expression: eʹ) {
  case .success(let exprType):
    if let meths = methods(ct: classTable, className: exprType) {
      return subtyping(ct: classTable, exprType, method.signature.typeName)
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
func classTyping(ct classTable: CT, context: Context, class: FJClass) -> Bool {
  //classTyping ct ctx cl@(Class c b _ attrs (Constr _ pc s ths) meths) =

  guard let fields = fields(ct: classTable, className: `class`.extends) else {
    return false
  }
  guard `class`.constructor.args == fields + `class`.fields else {
    return false
  }
  guard `class`.constructor.fieldInits.allSatisfy({ $0.fieldName == $0.argumentName }) else {
    return false
  }
  guard let absMeths = abstractMethods(ct: classTable, class: `class`.name) else {
    return false // Error obtaining abstract methods
  }

  let pʹ = `class`.constructor.args.map(\.name)
  let pʺ = `class`.constructor.superArgs + `class`.constructor.fieldInits.map(\.fieldName)

  return absMeths.isEmpty
    && (pʹ == pʺ)
    && `class`.methods.allSatisfy {
      methodTyping(ct: classTable, context: context, className: `class`.name, method: $0)
    }
}

/// Checks if an interface is well-formed.
/// - Returns: `true` for a well-formed interface, `false` otherwise.
func interfaceTyping(ct classTable: CT, context: Context, interface: FJInterface) -> Bool {
  return abstractMethods(ct: classTable, class: interface.name) != nil
    && interface.defaultMethods.allSatisfy {
      methodTyping(ct: classTable, context: context, className: interface.name, method: $0)
    }
}
