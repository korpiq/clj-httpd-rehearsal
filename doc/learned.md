- Added [ring "1.3.0"] to :dependencies in project.clj
- "lein deps" downloads deps or reports error

- LightTable docs works well only inside a lein project

- copied and successfully ran minimum Hello World sample from

  http://www.learningclojure.com/2013/01/getting-started-with-ring.html

- Done 1. Serve one static JSON string: copied minimal JSON response method from

  https://github.com/ring-clojure/ring-json

- Done 2. Serve one dynamically created JSON string
  - declared libraries used only in tests with-profile test in project.clj :profile :test
  - unit test with clojure.test or speclj; I prefer semantics of former ("is" over "should")
  - made message id unique with atom and hid it with let-over-lambda pattern
  - GUI browsers make an extra request for /favicon.ico on each page load

- next minimal goals:
  3. Serve infinite stream of JSON
  4. Allow clients to connect and disconnect at will
  5. Support some resend-state request to send "full state"
