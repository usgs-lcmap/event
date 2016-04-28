(ns ^{:doc
  "Database LCMAP REST Service system component

  For more information, see the module-level code comments in
  ``lcmap.event.components``."}
  lcmap.event.components.messaging
  (:require [clojure.tools.logging :as log]
            [com.stuartsierra.component :as component]
            [langohr.core :as rmq]
            [langohr.channel :as lch]
            [langohr.queue :as lq]
            [langohr.exchange :as le]
            [langohr.consumers :as lc]
            [langohr.basic :as lb]))

(defrecord MessagingClient [ ]
  component/Lifecycle

  (start [component]
    (log/info "Starting LCMAP Event messaging client ...")
    (let [msg-cfg (get-in component [:cfg :env :messaging])]
      (log/debug "Using config:" msg-cfg)
      (let [conn (rmq/connect msg-cfg)
            ch (lch/open conn)]
        (log/debug "Component keys:" (keys component))
        (log/debug "Successfully created messaging connection:" conn)
        (assoc component :conn conn)
        (assoc component :ch ch))))

  (stop [component]
    (log/info "Stopping LCMAP Event messaging client ...")
    (log/debug "Component keys" (keys component))
    (let [conn (:conn component)
             ch (:ch component)]
      (do (log/debug "Using connection object:" conn)
          (rmq/close ch)
          (rmq/close conn)))
    (assoc component :ch nil)
    (assoc component :conn nil)))

(defn new-messaging-client []
  (->MessagingClient))
