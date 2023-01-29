(ns httpd.acceptance-test
  (:require [clojure.test :refer :all]
            [clojure.core.async :refer [go]]
            [httpd.httpd-server :refer [server]]))

(def send-when-notified (Object.))

(defn chat-client [client-identifier]
  (let [responses (transient [])]
    ; FIXME: actually open stream
    ; FIXME: wait for start sign;
    ;(Thread/wait start-switch)
    ; FIXME: send a message
    (println "client" client-identifier "started.")
    ; collect responses
    '((send [message] (conj! responses message))
      (get-responses [] (persistent! responses))
       )))

(deftest concurrent-messages-passed-in-order
  (testing "Many clients sending simultaneously will all receive all messages in same order"
    (let [server-stopper (atom {})]
      (go #(swap! server-stopper { :stop (server) }))
      (dotimes [thread-number 1] #(go (fn [] (chat-client thread-number))))
      ;(Thread/notify send-when-notified)
      (:stop server-stopper)
      )))
