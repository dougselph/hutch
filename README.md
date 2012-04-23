# Hutch

RabbitMQ library for Clojure.

## Getting started

Simply add Hutch as a dependency to your lein project:

```clojure
[hutch "0.1.0"]
```


## Examples of using hutch:

```clojure

(use 'hutch.core)

; set connection properties for your RabbitMQ server
(def testRabbit {:host "localhost"
                 :vhost "/"
                 :port 5672
                 :username "hutchuser"
                 :password "mypassword"
                 :queuename "demoqueue"})

; How many messages you want to queue for your test.
(def msgCount 40000)

; Routing key
(def qkeyword "demoqueue")

; A function similar to this in your code pushes msgCount messages onto the queue
(defn push-to-queue [msgCount pubchan qkeyword]
  (dotimes [i msgCount]
    (publish pubchan qkeyword (. (format "Hutch Message %d" i) getBytes))
    ;(Thread/sleep 250)  ; Uncomment to have function sleep between messages
    )
  (println (format "\n%d messages published to queue\n" msgCount)))

; A function similar to this can consume messages from a queue in a loop
(defn consume-queue [consumer]
  (loop []
    (let [delivery (.nextDelivery consumer )
          str (String. (. delivery getBody))]
      (println str)
      (.basicAck channel (.. delivery getEnvelope getDeliveryTag) false)
      (recur))))

; Call the message publish function
(push-to-queue msgCount (publish-channel testRabbit) qkeyword))

; Call the queue consumer
(consume-queue (queue-consumer testRabbit))


```

## License

Copyright (C) 2012 Doug Selph

Distributed under the MIT License.
