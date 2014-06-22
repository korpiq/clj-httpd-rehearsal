(ns httpd.core (:gen-class))

(use
   '[ring.adapter.jetty :only [run-jetty]]
   '[ring.util.response :only [response]]
   '[ring.middleware.json :only [wrap-json-response]])

(defn handler [request] (response {:hello "World!"}))

(def app (wrap-json-response handler))

(defn -main
  "Run JSON REST HTTP service until stopped."
  [& args]
  ;; work around dangerous default behaviour in Clojure
  (alter-var-root #'*read-eval* (constantly false))
  (defonce server (run-jetty #'app {:port 8080 :join? false})))
