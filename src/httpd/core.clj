(ns httpd.core (:gen-class))

(use
  '[httpd.channel-collection]
  '[org.httpkit.server]
  '[ring.util.response]
  '[ring.middleware.json :only [wrap-json-response]])

(def stream-uri "/stream")
(def chat-uri "/chat.html")

(def all-channels (make-channel-collection))

(def next-message-id
  (let [message-id-counter (atom 0N)]
    (fn [] (swap! message-id-counter inc) @message-id-counter)))

(defn welcome-new-stream-listener [channel]
  (on-receive channel
              (fn [message]
                (let [id (next-message-id)
                      full-message (str id " | " message)]
                  (println "chat message:" full-message)
                  (send-to-channels all-channels full-message))))
  (collect-channel all-channels channel))

(defn async-stream-handler [request]
  (with-channel request channel (welcome-new-stream-listener channel)))

(defn request-mapper [request]
  (let [uri (str (:uri request))]
    (println "http request:" uri)
    (cond
      (.startsWith uri stream-uri) (async-stream-handler request)
      (.equals "/" uri) (redirect chat-uri)
      :else (file-response uri {:root "static"}))))

(def app request-mapper)

(defn -main
  "Run JSON REST HTTP service until stopped."
  [& args]
  ;; work around dangerous default behaviour in Clojure
  (alter-var-root #'*read-eval* (constantly false))
  (run-server #'app {:port 8080 :join? false}))