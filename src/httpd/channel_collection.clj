(ns httpd.channel-collection)

(def ^:private channels (atom #{}))

(defn has-channels? [] (not (empty? @channels)))

(defn add-channel [new-channel] (swap! channels #(conj % new-channel)) nil)

(defn remove-channel [closed-channel] (swap! channels #(disj % closed-channel)) nil)

(defn each-channel [callback] (doseq [channel @channels] (callback channel)))

(defn remove-all-channels [] (reset! channels #{}))
