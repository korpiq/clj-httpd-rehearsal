(ns httpd.core (:gen-class))

(use '[httpd.httpd-server])

(defn -main
  "Run JSON websocket HTTP service until stopped."
  [& args]
  ;; work around dangerous default behaviour in Clojure
  (alter-var-root #'*read-eval* (constantly false))
  (server))
