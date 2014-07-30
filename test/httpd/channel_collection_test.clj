(ns httpd.channel-collection-test
  (:require [clojure.test :refer :all]
            [httpd.channel-collection :refer :all]
            [org.httpkit.server :refer :all]))

(deftype mock-channel [atomic-map]
  Channel
  (open? [_] nil)
  (close [_] ((:close-callback @atomic-map) :test-close))
  (websocket? [_] nil)
  (send! [_ data] (swap! atomic-map #(assoc % :sent data)))
  (send! [_ data close-after-send?] (swap! atomic-map #(assoc % :sent data)))
  (on-receive [_ callback] nil)
  (on-close [_ callback] (swap! atomic-map #(assoc % :close-callback callback))))

(defn make-mock-channel [] (mock-channel. (atom {})))

(deftest can-add-and-remove-channels
  (testing "Added channels can be independently removed."
    (let [collected-channels (make-channel-collection)
          channel-1 (make-mock-channel)
          channel-2 (make-mock-channel)
          ]
      (is (not (contains-channels? collected-channels)))
      (collect-channel collected-channels channel-1)
      (is (contains-channels? collected-channels))
      (collect-channel collected-channels channel-2)
      (close channel-1)
      (is (contains-channels? collected-channels))
      (close channel-2)
      (is (not (contains-channels? collected-channels))))))

(deftest can-send-to-all-channels
  (testing "Can send message to each channel."
    (let [collected-channels (make-channel-collection)
          map-1 (atom {})
          map-2 (atom {})
          channel-1 (mock-channel. map-1)
          channel-2 (mock-channel. map-2)
          test-message "This is my test message. There are many but I like it."
          ]
      (is (not (contains-channels? collected-channels)))
      (collect-channel collected-channels channel-1)
      (collect-channel collected-channels channel-2)
      (send-to-channels collected-channels test-message)
      (is (identical? (:sent @map-1) test-message))
      (is (identical? (:sent @map-2) test-message)))))

(deftest can-send-to-no-channels
  (testing "Can send message when there are no channels."
    (let [collected-channels (make-channel-collection)]
      (send-to-channels collected-channels "lost in cyberspace")
      (is (not (contains-channels? collected-channels))))))
