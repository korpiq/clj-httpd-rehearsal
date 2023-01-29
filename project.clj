(defproject httpd "0.1.0-SNAPSHOT"
  :description "Rehearsal in concurrent message passing in Clojure"
  :url "https://github.com/korpiq/clj-httpd-rehearsal"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                  [org.clojure/clojure "1.11.1"]
                  [ring "1.9.6"]
                  [http-kit "2.1.16"]
                  [org.clojure/data.json "0.2.5"]
                  [javax.xml.bind/jaxb-api "2.3.0"]
                  ]
  :profiles {:test {:dependencies [[org.clojure/core.async "1.6.673"]]}}
  :main httpd.core)
