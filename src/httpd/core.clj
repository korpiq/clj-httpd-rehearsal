(ns httpd.core (:gen-class))

(use
   '[ring.adapter.jetty :only [run-jetty]]
   '[ring.util.response :only [response]]
   '[ring.middleware.json :only [wrap-json-response]])

(def next-message-id
  (let [message-id-counter (atom 0N)]
    (fn [] (swap! message-id-counter inc) @message-id-counter)))

(defn handler [request] (response {:msg-id (next-message-id)}))

(def app (wrap-json-response handler))

(defn -main
  "Run JSON REST HTTP service until stopped."
  [& args]
  ;; work around dangerous default behaviour in Clojure
  (alter-var-root #'*read-eval* (constantly false))
  (defonce server (run-jetty #'app {:port 8080 :join? false})))
