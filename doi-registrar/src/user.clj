(ns user
  (:require [mount.core :as mount]
            [xtdb.api :as xt]
            server
            datastore))

(defn start []
  (mount/start))

(defn node []
  datastore/xtdb-node)
