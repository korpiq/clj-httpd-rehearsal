# Introduction to `httpd`

TODO: write [great documentation](http://jacobian.org/writing/great-documentation/what-to-write/)

## Outline of how it works

### core

- `-main` starts a web server at port 8080
- `request-mapper` then serves
  - at `/` a web page containing chat client from under [/static](../static)
  - at `/stream` a web socket for exchanging chat messages
- `async-stream-handler` adds a new web socket to exchange chat messages with into the `channel-collection` `all-channels`
- `receive-message-from-channel` parses a message and sends it
  - `format-chat-message` checks message fields and adds a running `id` (atom)
  - `send-chat-message` sends to `all-channels`

### channel-collection

- `make-channel-collection` provides a new atomic Set for storing web sockets aka channels
- `channel-collection-type`
  - `collect-channel` adds channel to collection and sets it to remove itself thereof upon close
  - `send-to-channels` sequentially sends a message to every channel in collection
  - `contains-channels?` is used by tests to check that the collection is not empty.
