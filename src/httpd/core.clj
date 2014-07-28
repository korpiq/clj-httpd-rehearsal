(ns httpd.core (:gen-class))

(use
  '[httpd.channel-collection]
  '[org.httpkit.server]
  '[ring.util.response]
  '[ring.middleware.json :only [wrap-json-response]])

(def stream-uri "/stream")

(def next-message-id
  (let [message-id-counter (atom 0N)]
    (fn [] (swap! message-id-counter inc) @message-id-counter)))

(defn handler [request]
  (let [id-to-send (next-message-id)]
    (println "responding with " id-to-send)
    (response {:msg-id id-to-send})))

(def json-handler (wrap-json-response handler))

(defn async-sender []
  (let [message (str (:body (json-handler {})))]
    (println "send" message)
    (each-channel #(do (println "send" % message) (send! % message)))))

(defn websocket-handler [channel] :todo)
(defn long-poll-handler [channel]
  (async-sender)
  (async-sender)
  (close channel)
  )

(defn async-stream-handler [request]
  (with-channel request channel
                (add-channel channel)
                (println "open" channel)
                (on-close channel
                          (fn [status]
                            (println "close" channel " on " status)
                            (remove-channel channel)))
                (if (websocket? channel)
                  (websocket-handler channel)
                  (long-poll-handler channel))))

(defn request-mapper [request]
  (let [uri (str (:uri request))]
    (println uri)
    (cond
      (.startsWith uri stream-uri) (async-stream-handler request)
      (.equals "/" uri) (redirect stream-uri)
      :else (file-response uri {:root "static"}))))

(def app request-mapper)

(defn -main
  "Run JSON REST HTTP service until stopped."
  [& args]
  ;; work around dangerous default behaviour in Clojure
  (alter-var-root #'*read-eval* (constantly false))
  (run-server #'app {:port 8080 :join? false}))
