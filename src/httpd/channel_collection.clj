(ns httpd.channel-collection)

(use '[org.httpkit.server])

(defprotocol channel-collection-protocol
  "Collection for keeping and sending to multiple org.httpkit.server.AsyncChannels"
  (collect-channel [this channel])
  (send-to-channels [this message])
  (contains-channels? [this])
  )

(deftype channel-collection-type [atomic-set]
  channel-collection-protocol
  (collect-channel [_ channel]
    { :pre (isa? channel org.httpkit.server.Channel) }
    (on-close channel (fn [status]
                        (println "Detach" channel "on" status)
                        (swap! atomic-set #(disj % channel))
                        nil))
    (swap! atomic-set #(conj % channel))
    nil)
  (send-to-channels [_ message]
    (doseq [channel @atomic-set] (send! channel message false)))
  (contains-channels? [_] (not (empty? @atomic-set))))

(defn make-channel-collection [] (channel-collection-type. (atom #{})))
