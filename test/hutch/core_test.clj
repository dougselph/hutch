(ns hutch.core-test
  (:use clojure.test
        hutch.core))

;; tests assume a RabbitMQ server is available on localhost:5672,
;;   with username/pswd of guest/guest. Customizze for your
;;   environment as needed.

; set connection properties for your RabbitMQ server
(def testRabbit {:host "localhost"
                 :vhost "/"
                 :port 5672
                 :username "guest"
                 :password "guest"
                 :queuename "testq"})

(def qkeyword "testq")

(def msgCount 5)

(defn push-to-queue [msgCount pubchan qkeyword]
  (dotimes [i msgCount]
    (publish pubchan qkeyword (. (format "Hutch Message %d" i) getBytes))
    (def pubCount i)
    ;(Thread/sleep 250)  ; Uncomment to have function sleep between messages
    )
  (println (format "\n%d messages published to queue" pubCount)))

; A function similar to this can consume messages from a queue in a loop
(defn consume-queue [msgCount consumer]
  (dotimes [i msgCount]
    (let [delivery (.nextDelivery consumer )
          str (String. (. delivery getBody))]
      (.basicAck channel (.. delivery getEnvelope getDeliveryTag) false)
      (str))))

(deftest publish-test
  (testing "Test publishing messages to queue"
    (= "5 messages published to queue"
       (push-to-queue msgCount (publish-channel testRabbit) qkeyword))))

(deftest consume-test
  (testing "Test consuming messages from queue"
    (= 1 1)))

(println "Following is returned from consume-queue:\n")
(println (consume-queue msgCount (queue-consumer testRabbit)))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 0 1))))
