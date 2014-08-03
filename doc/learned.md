### Preparations

- Added [ring "1.3.0"] to :dependencies in project.clj
- "lein deps" downloads deps or reports error
- LightTable docs works well only inside a lein project
- copied and successfully ran minimum Hello World sample from
  [Learning Clojure: getting-started-with-ring](http://www.learningclojure.com/2013/01/getting-started-with-ring.html)

### Done 1: Serve one static JSON string

- copied minimal JSON response method from
  [Ring JSON README](https://github.com/ring-clojure/ring-json/blob/master/README.md)

### Done 2: Serve one dynamically created JSON string

- declared libraries used only in tests with-profile test in project.clj :profile :test
- unit test with clojure.test or speclj; I prefer semantics of former ("is" over "should")
- made message id unique with atom and hid it with let-over-lambda pattern
- GUI browsers make an extra request for /favicon.ico on each page load

### Fixed usage with browser

- ring.util.response provides easy redirect, file-response for static content, etc
- considered privatizing unnecessarily public core namespace members but with-meta :private is a bit verbose for that

### WebSocket library selection pains

- apparently the default ring.adapter.jetty does not support WebSockets due to its simplistic request-response model
- jetty7, http-kit, webbit, aleph, or pedestal?
- Ilari voted for http-kit; Kalle has difficulty picking any one over the others, so will try http-kit

### Object Oriented Design Pains

- OO can be done with defprotocol, defrecord, and deftype, or closures
- Clojure functional style preference is confusing when dealing with actually transient things
  such as collections of network connections.

### Done 3. Serve infinite stream of JSON

- web sockets work with very simple javascript on latest Chrome, Safari, Firefox.
- oops, todo: route messages through an agent (single thread) in clojure server to ensure equal order of messages to all clients.

### Done 4. Allow clients to connect and disconnect at will

- duh, this happens naturally when collecting initiated channels and handling their close events

### Next minimal goals

5. Support some resend-state request to send "full state"
