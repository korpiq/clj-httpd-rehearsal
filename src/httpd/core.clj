(ns httpd.core
  (:gen-class))

(defn app [request]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "Hello World"})

(require 'ring.adapter.jetty)

(defonce server
  (ring.adapter.jetty/run-jetty
   #'app {:port 8080 :join? false}))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  ;; work around dangerous default behaviour in Clojure
  (alter-var-root #'*read-eval* (constantly false))
  (println "Hello, World!"))
