(ns service.datastore
  (:require [clojure.java.io :as io]
            [mount.core :as mount]
            [xtdb.api :as xt]))

;; TODO move somewhere else?
(defn- format-date [date]
  (.format (java.text.SimpleDateFormat. "yyyy-MM-dd HH:mm") date))

;; TODO see above
(defn- now []
  (java.util.Date.))

(defn start-xtdb! []
  (letfn [(kv-store [dir]
            {:kv-store {:xtdb/module 'xtdb.rocksdb/->kv-store
                        :db-dir (io/file dir)
                        :sync? true}})]
    (xt/start-node
     {:xtdb/tx-log (kv-store "data/dev/tx-log")
      :xtdb/document-store (kv-store "data/dev/doc-store")
      :xtdb/index-store (kv-store "data/dev/index-store")})))

(defn stop-xtdb! [xtdb-node]
  (.close xtdb-node))

#_{:clj-kondo/ignore [:unresolved-symbol]}
(mount/defstate xtdb-node
  :start (do
           (tap> [:xtdb-node :up])
           (start-xtdb!))
  :stop (do
          (tap> [:xtdb-node :down])
          (stop-xtdb! xtdb-node)
          nil))

#_{:clj-kondo/ignore [:unresolved-var]}
(defn create [resource id]
  #_{:clj-kondo/ignore [:unresolved-symbol]}
  (xt/submit-tx xtdb-node
                [[::xt/put
                  (assoc resource
                         :xt/id id
                         :date  (now))]])
  #_{:clj-kondo/ignore [:unresolved-symbol]}
  (xt/sync xtdb-node))

#_{:clj-kondo/ignore [:unresolved-var]}
(defn get-by-id [id]
  (-> (xt/q #_{:clj-kondo/ignore [:unresolved-symbol]}
       (xt/db xtdb-node)
            '{:find  [(pull ?e [*])]
              :in    [id]
              :where [[?e :xt/id id]]}
            id)
      ffirst
      (update :date format-date)))

(defn get-all []
  (let [resources (map first (xt/q
                              #_{:clj-kondo/ignore [:unresolved-symbol]}
                              (xt/db xtdb-node)
                              '{:find     [(pull ?e [*]) date]
                                :where    [[?e :date date]]
                                :order-by [[date :desc]]}))]

    (map #(update % :date format-date) resources)))
