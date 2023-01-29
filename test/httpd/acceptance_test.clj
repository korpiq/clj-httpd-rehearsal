(ns httpd.acceptance-test
  (:require [clojure.test :refer :all]
            [clojure.core.async :refer [go chan pub sub put! <!!]]
            [clojure.data.json :as json]
            [gniazdo.core :refer :all]
            [httpd.httpd-server :refer [server]]))

(def num-clients 10)
(def connected-count (atom 0))
(def received-count (atom 0))
(def expect-to-receive-num-messages (* num-clients num-clients))
(def triggers-channel (chan 1))
(def triggers-pub (pub triggers-channel identity))
(def results-channel (chan num-clients))

(defn chat-client [client-identifier]
  (let [responses (transient [])
        websocket (connect "ws://127.0.0.1:8080/stream"
                                :on-receive (fn [received]
                                              (conj! responses (json/read-str received))
                                              (swap! received-count inc)
                                             )
                   )
        wait-to-send-channel (chan 1)
        all-received-channel (chan 1)
        ]

        (sub triggers-pub :send-now wait-to-send-channel)
        (sub triggers-pub :all-received all-received-channel)
        (swap! connected-count inc)
        (<!! wait-to-send-channel)
        (send-msg websocket (json/write-str { :username (str "client" client-identifier) :message (str "Hello from " client-identifier) }))
        (<!! all-received-channel)
        (put! results-channel { :client-id client-identifier :responses (persistent! responses)})
        ))

(defn until-timeout-or-condition [milliseconds check]
  (let [start-time (System/currentTimeMillis)]
    (while (and
             (> milliseconds (- (System/currentTimeMillis) start-time))
             (not (check)))
      (Thread/sleep 10))
    ))

(defn collect-results []
  (let [results (transient {})]
    (dotimes [result-number @connected-count]
      (let [result (<!! results-channel)]
        (conj! results [(:client-id result) (:responses result)])))
    (persistent! results)
    )
  )

(deftest concurrent-messages-passed-in-order
  (testing "Many clients sending simultaneously will all receive all messages in same order"
    (let [stop-server (server)]
      (dotimes [thread-number num-clients] (.start (Thread. #(chat-client thread-number))))
      (println-str "Waiting for " num-clients " clients to connect...")
      (until-timeout-or-condition 2000 #(= @connected-count num-clients))
      (is (= @connected-count num-clients) "All clients can connect")
      (put! triggers-channel :send-now)
      (let [num-ok-clients @connected-count expect-to-receive-num-messages (* num-ok-clients num-ok-clients)]
        (println-str "Waiting until all " num-ok-clients " clients have received all messages from each other...")
        (until-timeout-or-condition (* expect-to-receive-num-messages 50) #(= @received-count expect-to-receive-num-messages))
        (is (= (deref received-count) expect-to-receive-num-messages) "Each client receives all messages")
        (put! triggers-channel :all-received)
        (let [results (collect-results)]
          (dotimes [row-number num-ok-clients]
            (is (= (count (get results row-number)) num-ok-clients) (str "Client#" row-number " should have one message from each client."))
            (dotimes [client-number (- num-ok-clients 1)]
              (let [first-client-row (get (get results client-number) row-number)
                    next-client-number (+ client-number 1)
                    next-client-row (get (get results next-client-number) row-number)]
                (is (= first-client-row next-client-row) (str "Row#" row-number " of client#" client-number " and client#" next-client-number " match" ))
                )
              )
            )
          )
        )
      (stop-server)
      )))
