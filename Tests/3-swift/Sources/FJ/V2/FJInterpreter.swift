// Based on <https://github.com/fjpub/fj-lam/blob/cbcb453fb2595d341bb3c1341fdf235f066778fd/V2/FJInterpreter.hs>.

/// Evaluates an expression.
/// - Returns: An expression after processing one reduction step.
func evalʹ(ct classTable: CT, expr: FJExpr) -> FJExpr? {
  switch expr {
  case let .createObject(className, params): // RC-New-Arg
    let pʹ = params.compactMap { evalʹ(ct: classTable, expr: $0) }
    return .createObject(className, pʹ)

  case .fieldAccess(let expr, let fieldName):
    if isValue(ct: classTable, expr) { // R-Field
      switch expr {
      case let .createObject(className, params):
        guard let fields = fields(ct: classTable, className: className) else {
          assertionFailure()
          return nil
        }
        guard let index = fields.firstIndex(where: { $0.name == fieldName }) else {
          assertionFailure()
          return nil
        }
        return lambdaMark(expression: params[index], type: fields[index].name)
      default:
        return nil // Not an object instance
      }
    } else { // RC-Field
      return evalʹ(ct: classTable, expr: expr).map { .fieldAccess($0, fieldName) }
    }

  case let .methodInvocation(source, methodName, methodParams):
    if isValue(ct: classTable, source) {
      if methodParams.allSatisfy({ isValue(ct: classTable, $0) }) {
        // R-Invk
        switch source {
        case let .createObject(className, _): // R-Invk
          guard let (methodParameterNames, methodBody) = methodBody(
            ct: classTable,
            methodName: methodName,
            className: className
          ) else { return nil } // No method body
          guard let (methodArgTypes, methodReturnType) = methodType(
            ct: classTable,
            methodName: methodName,
            className: className
          ) else { return nil } // No method type
          let pʹ = zip(methodParams, methodArgTypes).map(lambdaMark)
          return substitute(
            parameterNames: methodParameterNames + ["this"],
            parameters: pʹ + [source],
            body: lambdaMark(expression: methodBody, type: methodReturnType)
          )
        case let .cast(className, .lambda(lambdaParams, lambdaBody)):
          guard let (methodArgTypes, methodReturnType) = methodType(
            ct: classTable,
            methodName: methodName,
            className: className
          ) else { return nil } // No method type
          let pʹ = zip(methodParams, methodArgTypes).map(lambdaMark)

          if let (methodParameterNames, methodBody) = methodBody(
            ct: classTable,
            methodName: methodName,
            className: className
          ) { // R-Default
            return substitute(
              parameterNames: methodParameterNames,
              parameters: pʹ,
              body: lambdaMark(expression: methodBody, type: methodReturnType)
            )
          } else { // R-Lam
            return substitute(
              parameterNames: lambdaParams.map(\.name),
              parameters: pʹ,
              body: lambdaMark(expression: lambdaBody, type: methodReturnType)
            )
          }
        default:
          return nil
        }
      } else { // RC-Invk-Arg
        let pʹ = methodParams.compactMap { evalʹ(ct: classTable, expr: $0) }
        return .methodInvocation(source, methodName, pʹ)
      }
    } else { // RC-Invk-Recv
      return evalʹ(ct: classTable, expr: source).map {
        .methodInvocation($0, methodName, methodParams)
      }
    }

  case let .cast(castType, castExpr):
    if isValue(ct: classTable, castExpr) {
      switch castExpr {
      case let .createObject(type, _):
        if subtyping(ct: classTable, type, castType) { // R-Cast
          return castExpr
        } else {
          return nil
        }
      case let .cast(lambdaType, .lambda):
        if subtyping(ct: classTable, lambdaType, castType) { // R-Cast-Lam
          return castExpr
        } else {
          return nil
        }
      default:
        return expr // Annotated lambda expression is a value
      }
    } else { // RC-Cast
      return evalʹ(ct: classTable, expr: castExpr).map { FJExpr.cast(castType, $0) }
    }

  case .lambda:
    return expr

  case .variable:
    return nil
  }
}

/// Evaluates an expression recursively.
/// - Returns: A value after all the reduction steps.
func eval(ct classTable: CT, expr: FJExpr) -> FJExpr {
  if isValue(ct: classTable, expr) {
    return expr
  } else {
    return eval(ct: classTable, expr: evalʹ(ct: classTable, expr: expr) ?? expr)
  }
}

/// Replaces actual parameters in method body expression.
/// - Returns: A new changed expression.
func substitute(parameterNames: [String], parameters: [FJExpr], body: FJExpr) -> FJExpr? {
  switch body {
  case .variable(let varName):
    return parameterNames.firstIndex(of: varName).map { parameters[$0] }
  case let .fieldAccess(source, fieldName):
    return substitute(parameterNames: parameterNames, parameters: parameters, body: source)
      .map { FJExpr.fieldAccess($0, fieldName) }
  case let .methodInvocation(source, methodName, methodParams):
    let methodParams = methodParams.compactMap {
      substitute(parameterNames: parameterNames, parameters: parameters, body: $0)
    }
    return substitute(parameterNames: parameterNames, parameters: parameters, body: source)
      .map { FJExpr.methodInvocation($0, methodName, methodParams) }
  case let .createObject(className, parameters):
    let parameters = parameters.compactMap {
      substitute(parameterNames: parameterNames, parameters: parameters, body: $0)
    }
    return .createObject(className, parameters)
  case let .cast(castType, expr):
    return substitute(parameterNames: parameterNames, parameters: parameters, body: expr)
      .map { FJExpr.cast(castType, $0) }
  case .lambda:
    return body // Do nothing
  }
}
