(ns hutch.core
  (:import (com.rabbitmq.client ConnectionFactory
                                Connection
                                Channel
                                QueueingConsumer)))

; if true, received messages will be Ack-ed immediately
(def autoAck false)

(defn make-factory
  "Make AMQP connection factory"
  [vhost host port username password]
  (doto (ConnectionFactory.)
    (.setVirtualHost vhost)
    (.setUsername username)
    (.setPassword password)
    (.setHost host)
    (.setPort 5672)))

(defn breeder
  "Connect to RabbitMQ and return connection"
  [vhost host port username password]
  (let [factory (make-factory vhost host port username password)]
    (. factory newConnection)))

(defn publish
  "Publish message: msg to channel using keyword: key"
  [channel key msg]
  (. channel basicPublish "" key nil msg))

(defn publish-channel
  "Return channel reference to caller for message publishing"
  [conn-deets]
  (let [host (:host conn-deets)
        vhost (:vhost conn-deets)
        port (:port conn-deets)
        username (:username conn-deets)
        password (:password conn-deets)
        queuename (:queuename conn-deets)]
    (do
      (def connection (breeder vhost host port username password))
      (def channel (. connection createChannel))
      ; args are queuename durable exclusive auto-delete nil
      (. channel queueDeclare queuename true false false nil)
      channel)))

(defn queue-consumer
  "Return channel consumer reference to caller"
  [conn-deets]
  (let [host (:host conn-deets)
        vhost (:vhost conn-deets)
        port (:port conn-deets)
        username (:username conn-deets)
        password (:password conn-deets)
        queuename (:queuename conn-deets)
        connection (breeder vhost host port username password)]
    (do
      (def channel (. connection createChannel))
      (def consumer (new QueueingConsumer channel))
      (. channel basicConsume queuename autoAck consumer)
      consumer)))
