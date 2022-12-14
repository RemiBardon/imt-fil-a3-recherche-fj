import XCTest
import FJ

final class FJParserTests: XCTestCase {
  /// This test represents a Java program.
  /// If it compiles, then it means the program can be represented with values.
  ///
  /// ```java
  /// class A extends Object {
  ///   A() { super(); }
  /// }
  /// class B extends Object {
  ///   B() { super(); }
  /// }
  /// class Pair extends Object {
  ///   Object fst;
  ///   Object snd;
  ///   Pair(X fst, Y snd) {
  ///     super();
  ///     this.fst = fst;
  ///     this.snd = snd;
  ///   }
  ///   Pair setfst(Object newfst) {
  ///     return new Pair(newfst, this.snd);
  ///   }
  /// }
  /// ```
  func testProgramsCanBeRepresented() throws {
    let _: [FJType] = [
      .class(.init(
        name: "A",
        extends: "Object",
        constructor: .init(name: "A")
      )),
      .class(.init(
        name: "B",
        extends: "Object",
        constructor: .init(name: "B")
      )),
      .class(.init(
        name: "Pair",
        extends: "Object",
        fields: [
          .init(type: "Object", name: "fst"),
          .init(type: "Object", name: "snd"),
        ],
        constructor: .init(
          name: "Pair",
          args: [
            .init(type: "X", name: "fst"),
            .init(type: "Y", name: "snd"),
          ],
          fieldInits: [
            .init(fieldName: "fst", argumentName: "fst"),
            .init(fieldName: "snd", argumentName: "snd"),
          ]
        ),
        methods: [
          .init(
            signature: .init(
              returnTypeName: "Pair",
              name: "setfst",
              args: [
                .init(type: "Object", name: "newfst"),
              ]
            ),
            body: .createObject(className: "Pair", arguments: [
              .variable(name: "newfst"),
              .fieldAccess(source: .variable(name: "this"), fieldName: "snd"),
            ])
          ),
        ]
      )),
    ]
  }
}
