(ns api
  (:require [xtdb.api :as xt]
            datastore
            scraper))

#_{:clj-kondo/ignore [:unresolved-var]}
(defn- fetch-resources []
  (let [resources (map first (xt/q
                              (xt/db datastore/xtdb-node)
                              '{:find     [(pull ?e [*]) date]
                                :where    [[?e :date date]]
                                :order-by [[date :desc]]
                                }))]
    
    (map (fn [{date :date :as resource}]
           (assoc resource :date (.format (java.text.SimpleDateFormat. "yyyy-MM-dd HH:mm") date)))
         resources)))

(defn get-handler [_]
  {:body {:resources (fetch-resources)}})

#_{:clj-kondo/ignore [:unresolved-var]}
(defn handler [{{url :url} :body}]
  (let [doi (scraper/take-screenshot! url)]
      {:body {:doi doi
              :resources (fetch-resources)}}))
