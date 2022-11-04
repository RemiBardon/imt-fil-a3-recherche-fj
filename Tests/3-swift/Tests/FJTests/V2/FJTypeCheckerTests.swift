import XCTest
@testable import FJ

final class FJTypeCheckerTests: XCTestCase {
  func testClassTyping() {
    let wellTypedClass = FJClass(
      name: "MyClass",
      extends: "Object",
      implements: [],
      fields: [
        .init(type: "string", name: "param"),
      ],
      constructor: .init(
        name: "MyClass",
        args: [
          .init(type: "string", name: "param"),
        ],
        superArgs: [],
        fieldInits: [
          .init(fieldName: "param", argumentName: "param"),
        ]
      ),
      methods: []
    )
    XCTAssertTrue(classTyping(
      ct: ["MyClass": .class(wellTypedClass)],
      context: [:],
      class: wellTypedClass
    ))

    let badlyTypedClass = FJClass(
      name: "MyClass",
      extends: "Object",
      implements: [],
      fields: [
        .init(type: "int", name: "param"),
      ],
      constructor: .init(
        name: "MyClass",
        args: [
          .init(type: "string", name: "param"),
        ],
        superArgs: [],
        fieldInits: [
          .init(fieldName: "param", argumentName: "param"),
        ]
      ),
      methods: []
    )
    XCTAssertFalse(classTyping(
      ct: ["MyClass": .class(badlyTypedClass)],
      context: [:],
      class: badlyTypedClass
    ))
  }
}
