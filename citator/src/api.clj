(ns api
  (:require [xtdb.api :as xt]
            datastore
            scraper))

#_{:clj-kondo/ignore [:unresolved-var]}
(defn- fetch-resources []
  (let [resources (xt/q
                   (xt/db datastore/xtdb-node)
                   '{:find  [e url]
                     :where [[e :user/name "citator"]
                             [e :url url]]})]
    (map (fn [[doi url]] {:doi doi
                          :url url}) resources)))


(defn get-handler [_]
  {:body {:resources (fetch-resources)}})

#_{:clj-kondo/ignore [:unresolved-var]}
(defn handler [{{url :url} :body}]
  (let [doi (scraper/take-screenshot! url)]
      {:body {:doi doi
              :resources (fetch-resources)}}))
