(ns user
  (:require [mount.core :as mount]
            [xtdb.api :as xt]
            server
            [service.datastore :as datastore]))

(defn start []
  (mount/start))

(defn node []
  datastore/xtdb-node)
