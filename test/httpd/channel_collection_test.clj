(ns httpd.channel-collection-test
  (:require [clojure.test :refer :all]
            [httpd.channel-collection :refer :all]))

(deftest can-add-and-remove-channel
  (testing "Added channel can be removed from channels."
    (remove-all-channels)
    (is (not (has-channels?)))
    (add-channel ::dummy-1)
    (is (has-channels?))
    (remove-channel ::dummy-1)
    (is (not (has-channels?)))))

(deftest remove-leaves-other-channel
  (testing "Removing nonexistent channel leaves existing channel intact."
    (remove-all-channels)
    (is (not (has-channels?)))
    (add-channel ::dummy-2)
    (is (has-channels?))
    (remove-channel ::dummy-3)
    (is (has-channels?))))

(deftest can-send-to-all-channels
  (testing "Can send message to each channel."
    (remove-all-channels)
    (let [caught (atom [])]
      (add-channel ::dummy-4)
      (add-channel ::dummy-5)
      (each-channel (fn [channel] (swap! caught #(merge % channel))))
      (is (= @caught [::dummy-4 ::dummy-5])))))

(deftest can-send-to-no-channels
  (testing "Can send message when there are no channels."
    (remove-all-channels)
    (let [caught (atom [])]
      (each-channel (fn [channel] (swap! caught #(merge % channel))))
      (is (= @caught [])))))
