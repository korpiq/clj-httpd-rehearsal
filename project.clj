(defproject httpd "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"] [ring "1.3.0"] [ring/ring-json "0.3.1"] [http-kit "2.1.16"]]
  :profiles {:test {:dependencies [[org.clojure/data.json "0.2.5"]]}}
  :main httpd.core)
