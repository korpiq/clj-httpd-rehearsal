(ns httpd.httpd-server
  (:require [clojure.data.json :as json]))

(use
  '[httpd.channel-collection]
  '[org.httpkit.server]
  '[ring.util.response]
  '[ring.util.mime-type])

(def stream-uri "/stream")
(def chat-uri "/chat.html")
(def static-files-dir "static")

(def all-channels (make-channel-collection))

(def next-message-id
  (let [message-id-counter (atom 0N)]
    (fn [] (swap! message-id-counter inc) @message-id-counter)))

(defn send-chat-message [chat-message]
  (println "send chat message:" chat-message)
  (send-to-channels all-channels chat-message))

(defn format-chat-message [message]
  { :pre [(= ["message" "username"] (sort (keys message)))
          (re-matches #"^\p{L}{3,12}\p{N}{0,6}$" (get message "username"))
          (re-matches #"^[\p{L}\x20-\x40]{1,99}$" (get message "message"))]}
  (json/write-str (assoc message :id (next-message-id))))

(defn receive-message-from-channel [channel json-message]
  (try (-> json-message json/read-str format-chat-message send-chat-message)
       (catch AssertionError e
         (send! channel (json/write-str {:error (.getMessage e) })))))

(defn welcome-new-stream-listener [channel]
  (on-receive channel #(receive-message-from-channel channel %))
  (collect-channel all-channels channel))

(defn async-stream-handler [request]
  (with-channel request channel (welcome-new-stream-listener channel)))

(defn file-response-with-mimetype [uri]
  (let [response (file-response uri {:root static-files-dir})]
    (content-type response (ext-mime-type (.getName (:body response))))))

(defn request-mapper [request]
  (let [uri (str (:uri request))]
    (println "http request:" uri)
    (cond
      (.startsWith uri stream-uri) (async-stream-handler request)
      (.equals "/" uri) (redirect chat-uri)
      :else (file-response-with-mimetype uri))))

(defn server []
  (run-server #'request-mapper {:port 8080 :join? false}))
