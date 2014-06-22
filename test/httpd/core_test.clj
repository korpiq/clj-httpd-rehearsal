(ns httpd.core-test
  (:require [clojure.test :refer :all]
            [httpd.core :refer :all]
            [clojure.data.json :as json]))

(deftest app-response-is-valid-test
  (let [actual_result (app {})]
    (testing "app will return valid headers for JSON as a response to a request."
      (is (= 200 (:status actual_result)))
      (is (= {"Content-Type" "application/json; charset=utf-8"} (:headers actual_result))))
    (testing "app will return valid JSON in body of a response to a request."
      (is (map? (json/read-str (:body actual_result)))))))
