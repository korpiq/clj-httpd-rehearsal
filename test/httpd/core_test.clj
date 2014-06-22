(ns httpd.core-test
  (:require [clojure.test :refer :all]
            [httpd.core :refer :all]))

(deftest app-ok-test
  (testing "app will return valid JSON as a response to a request."
    (let [expected_result
          {:status 200,
           :headers {"Content-Type" "application/json; charset=utf-8"},
           :body "{\"hello\":\"World!\"}"}]
      (is (= expected_result (app {}))))))
