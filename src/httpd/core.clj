(ns httpd.core (:gen-class))

(use
   '[org.httpkit.server]
   '[ring.util.response]
   '[ring.middleware.json :only [wrap-json-response]])

(def message-uri "/counter")

(def next-message-id
  (let [message-id-counter (atom 0N)]
    (fn [] (swap! message-id-counter inc) @message-id-counter)))

(defn handler [request] (response {:msg-id (next-message-id)}))
(def json-handler (wrap-json-response handler))

(defn request-mapper [request]
  (let [uri (:uri request)]
    (cond
       (.equals "/" uri) (redirect message-uri)
       (.equals message-uri uri) (json-handler request)
       :else (file-response uri {:root "static"}))))

(def app request-mapper)

(defn -main
  "Run JSON REST HTTP service until stopped."
  [& args]
  ;; work around dangerous default behaviour in Clojure
  (alter-var-root #'*read-eval* (constantly false))
  (run-server #'app {:port 8080 :join? false}))
