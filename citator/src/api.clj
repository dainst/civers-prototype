(ns api
  (:require [xtdb.api :as xt]
            datastore
            scraper))

#_{:clj-kondo/ignore [:unresolved-var]}
(defn get-handler [_]
  (let [resources (xt/q 
                   (xt/db datastore/xtdb-node)
                   '{:find  [e url]
                     :where [[e :user/name "citator"]
                             [e :url url]]})
        resources (map (fn [[doi url]] {:doi doi
                                        :url url}) resources)]
    {:body {:resources resources}}))

#_{:clj-kondo/ignore [:unresolved-var]}
(defn handler [{{url :url} :body}]
  (let [doi (scraper/take-screenshot url)
        resources (xt/q (xt/db datastore/xtdb-node) 
                          '{:find  [e url]
                            :where [[e :user/name "citator"]
                                    [e :url url]]})
          resources (map (fn [[doi url]] {:doi doi
                                         :url url}) resources)]
      {:body {:doi doi
              :resources resources}}))
