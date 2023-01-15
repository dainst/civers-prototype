(ns datastore
  (:require [clojure.java.io :as io]
            [mount.core :as mount]
            [xtdb.api :as xt]))

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
