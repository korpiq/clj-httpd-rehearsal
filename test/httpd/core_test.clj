(ns httpd.core-test
  (:require [clojure.test :refer :all]
            [httpd.core :refer :all]
            [clojure.data.json :as json]))

(def last-response nil)
(defn next-response [] (def last-response (app {:uri message-uri})) last-response)
(defn get-response [] (or last-response (next-response)))
(defn get-response-body [] (:body (get-response)))
(defn get-response-body-json [] (json/read-str (get-response-body)))
(defn get-message-id [] ((get-response-body-json) "msg-id"))
(defn get-next-message-id [] (next-response) (get-message-id))

(deftest app-response-headers-valid-for-json-test
  (testing "app will return valid headers for JSON as a response to a request."
    (is (= 200 (:status (get-response)))
    (is (= {"Content-Type" "application/json; charset=utf-8"} (:headers (get-response)))))))

(deftest app-response-body-is-valid-json-test
  (testing "app will return valid JSON in body of a response to a request."
    (is (map? (get-response-body-json)))))

(deftest app-response-body-json-contains-id-test
  (testing "app response contains valid message id field"
    (is (integer? (get-message-id)))))

(let [first-id (get-next-message-id)
      second-id (get-next-message-id)
      third-id (get-next-message-id)]
  (deftest app-response-id-grows-test
    (testing "app response message id is bigger on each subsequent call")
      (is (< first-id second-id))
      (is (< second-id third-id))))
