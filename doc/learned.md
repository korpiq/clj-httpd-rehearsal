- Added [ring "1.3.0"] to :dependencies in project.clj
- "lein deps" downloads deps or reports error

- LightTable docs works well only inside a lein project

- copied and successfully ran minimum Hello World sample from

http://www.learningclojure.com/2013/01/getting-started-with-ring.html

- next minimal goals:
  1. Serve one static JSON string
  2. Serve one dynamically created JSON string
  3. Serve infinite stream of JSON
  4. Allow clients to connect and disconnect at will
  5. Support some resend-state request to send "full state"
