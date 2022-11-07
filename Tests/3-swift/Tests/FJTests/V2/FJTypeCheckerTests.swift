import XCTest
import FJ

final class FJTypeCheckerTests: XCTestCase {
  func testClassTyping() {
    let wellTypedClass = FJClass(
      name: "MyClass",
      extends: "Object",
      fields: [
        .init(type: "string", name: "param"),
      ],
      constructor: .init(
        name: "MyClass",
        args: [
          .init(type: "string", name: "param"),
        ],
        fieldInits: [
          .init(fieldName: "param", argumentName: "param"),
        ]
      )
    )
    XCTAssertTrue(classTyping(
      classTable: ["MyClass": .class(wellTypedClass)],
      context: [:],
      class: wellTypedClass
    ))

    let badlyTypedClass = FJClass(
      name: "MyClass",
      extends: "Object",
      fields: [
        .init(type: "int", name: "param"),
      ],
      constructor: .init(
        name: "MyClass",
        args: [
          .init(type: "string", name: "param"),
        ],
        fieldInits: [
          .init(fieldName: "param", argumentName: "param"),
        ]
      )
    )
    XCTAssertFalse(classTyping(
      classTable: ["MyClass": .class(badlyTypedClass)],
      context: [:],
      class: badlyTypedClass
    ))
  }
}
