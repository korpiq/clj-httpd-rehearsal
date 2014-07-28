(ns httpd.channel-collection)

(def channels (atom []))

(defn has-channels? [] (seq @channels))

(defn add-channel [new-channel] (swap! channels #(merge % new-channel)))

(defn remove-channel [closed-channel]
  (swap! channels
         (fn [old-channels]
           (filterv #(not (identical? closed-channel %)) old-channels)))
  nil)

(defn each-channel [callback] (doseq [channel @channels] (callback channel)))

(defn remove-all-channels [] (reset! channels []))
