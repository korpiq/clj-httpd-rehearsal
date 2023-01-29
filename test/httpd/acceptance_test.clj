(ns httpd.acceptance-test
  (:require [clojure.test :refer :all]
            [clojure.core.async :refer [go chan pub sub put! <!!]]
            [clojure.data.json :as json]
            [gniazdo.core :refer :all]
            [httpd.httpd-server :refer [server]]))

(def num-clients 2)
(def connected-count (atom 0))
(def received-count (atom 0))
(def expect-to-receive-num-messages (* num-clients num-clients))
(def triggers-channel (chan 1))
(def triggers-pub (pub triggers-channel identity))

(defn chat-client [client-identifier]
  (let [responses (transient [])
        websocket (connect "ws://127.0.0.1:8080/stream"
                                :on-receive (fn [received]
                                              (conj! responses received)
                                              (if (= (swap! received-count inc) expect-to-receive-num-messages)
                                                (put! triggers-channel :all-received))
                                                (println (str "client#" client-identifier " received: " received))
                                              ))
        wait-to-send-channel (chan 1)
        all-received-channel (chan 1)
        ]

        (sub triggers-pub :send-now wait-to-send-channel)
        (sub triggers-pub :all-received all-received-channel)
        (println (str "client#" client-identifier " connected."))
        (if (= (swap! connected-count inc) num-clients)
          (put! triggers-channel :send-now)
          (println (str "client#" client-identifier " got send trigger " (<!! wait-to-send-channel))))
        (send-msg websocket (json/write-str { :username (str "client" client-identifier) :message (str "Hello from " client-identifier) }))
        (println (str "client#" client-identifier " got finished trigger " (<!! all-received-channel)))
        ))

(deftest concurrent-messages-passed-in-order
  (testing "Many clients sending simultaneously will all receive all messages in same order"
    (let [stop-server (server) wait-to-send-channel (chan 1) all-received-channel (chan 1)]
      (sub triggers-pub :send-now wait-to-send-channel)
      (sub triggers-pub :all-received all-received-channel)
      (dotimes [thread-number num-clients] (go (chat-client thread-number)))
      (println "Waiting until all clients have connected...")
      (println "All clients have connected: " (<!! wait-to-send-channel))
      (println "Waiting until all clients have received all messages from each client...")
      (println "All messages received: " (<!! all-received-channel))
      (stop-server)
      )))
