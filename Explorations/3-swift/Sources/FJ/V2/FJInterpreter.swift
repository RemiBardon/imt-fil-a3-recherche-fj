// Based on <https://github.com/fjpub/fj-lam/blob/cbcb453fb2595d341bb3c1341fdf235f066778fd/V2/FJInterpreter.hs>.

/// Evaluates an expression.
/// - Returns: An expression after processing one reduction step.
func evalʹ(classTable: ClassTable, expr: FJExpr) -> FJExpr? {
  switch expr {
  case let .createObject(className, params): // RC-New-Arg
    let pʹ = params.compactMap { evalʹ(classTable: classTable, expr: $0) }
    return .createObject(className: className, arguments: pʹ)

  case .fieldAccess(let expr, let fieldName):
    if isValue(classTable: classTable, expr) { // R-Field
      switch expr {
      case let .createObject(className, params):
        guard let fields = classFields(classTable: classTable, className: className) else {
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
      return evalʹ(classTable: classTable, expr: expr)
        .map { .fieldAccess(source: $0, fieldName: fieldName) }
    }

  case let .methodInvocation(source, methodName, methodParams):
    if isValue(classTable: classTable, source) {
      if methodParams.allSatisfy({ isValue(classTable: classTable, $0) }) {
        // R-Invk
        switch source {
        case let .createObject(className, _): // R-Invk
          guard let (methodParameterNames, methodBody) = methodBody(
            classTable: classTable,
            methodName: methodName,
            className: className
          ) else { return nil } // No method body
          guard let (methodArgTypes, methodReturnType) = methodType(
            classTable: classTable,
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
            classTable: classTable,
            methodName: methodName,
            className: className
          ) else { return nil } // No method type
          let pʹ = zip(methodParams, methodArgTypes).map(lambdaMark)

          if let (methodParameterNames, methodBody) = methodBody(
            classTable: classTable,
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
        let pʹ = methodParams.compactMap { evalʹ(classTable: classTable, expr: $0) }
        return .methodInvocation(source: source, methodName: methodName, parameters: pʹ)
      }
    } else { // RC-Invk-Recv
      return evalʹ(classTable: classTable, expr: source).map {
        .methodInvocation(source: $0, methodName: methodName, parameters: methodParams)
      }
    }

  case let .cast(castType, castExpr):
    if isValue(classTable: classTable, castExpr) {
      switch castExpr {
      case let .createObject(type, _):
        if isSubtype(classTable: classTable, type, castType) { // R-Cast
          return castExpr
        } else {
          return nil
        }
      case let .cast(lambdaType, .lambda):
        if isSubtype(classTable: classTable, lambdaType, castType) { // R-Cast-Lam
          return castExpr
        } else {
          return nil
        }
      default:
        return expr // Annotated lambda expression is a value
      }
    } else { // RC-Cast
      return evalʹ(classTable: classTable, expr: castExpr)
        .map { FJExpr.cast(typeName: castType, expression: $0) }
    }

  case .lambda:
    return expr

  case .variable:
    return nil
  }
}

/// Evaluates an expression recursively.
/// - Returns: A value after all the reduction steps.
public func eval(classTable: ClassTable, expr: FJExpr) -> FJExpr {
  if isValue(classTable: classTable, expr) {
    return expr
  } else {
		return evalʹ(classTable: classTable, expr: expr)
			.map { eval(classTable: classTable, expr: $0) } ?? expr
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
      .map { FJExpr.fieldAccess(source: $0, fieldName: fieldName) }
  case let .methodInvocation(source, methodName, methodParams):
    let methodParams = methodParams.compactMap {
      substitute(parameterNames: parameterNames, parameters: parameters, body: $0)
    }
    return substitute(parameterNames: parameterNames, parameters: parameters, body: source)
      .map { FJExpr.methodInvocation(source: $0, methodName: methodName, parameters: methodParams) }
  case let .createObject(className, parameters):
    let parameters = parameters.compactMap {
      substitute(parameterNames: parameterNames, parameters: parameters, body: $0)
    }
    return .createObject(className: className, arguments: parameters)
  case let .cast(castType, expr):
    return substitute(parameterNames: parameterNames, parameters: parameters, body: expr)
      .map { FJExpr.cast(typeName: castType, expression: $0) }
  case .lambda:
    return body // Do nothing
  }
}
