(ns api
  (:require [xtdb.api :as xt]
            datastore
            scraper))

(defn- format-date [date]
  (.format (java.text.SimpleDateFormat. "yyyy-MM-dd HH:mm") date))

(defn- fetch-resources [node]
  (let [resources (map first (xt/q
                              (xt/db node)
                              '{:find     [(pull ?e [*]) date]
                                :where    [[?e :date date]]
                                :order-by [[date :desc]]
                                }))]
    
    (map #(update % :date format-date) resources)))

#_{:clj-kondo/ignore [:unresolved-var]}
(defn- fetch-resource [node doi]
 (-> (xt/q (xt/db node)
           '{:find  [(pull ?e [*])]
             :in    [doi]
             :where [[?e :xt/id doi]]}
           doi)
     ffirst
     (update :date format-date)))

#_{:clj-kondo/ignore [:unresolved-var]}
(defn get-handler [_]
  {:body {:resources (fetch-resources datastore/xtdb-node)}})

#_{:clj-kondo/ignore [:unresolved-var]}
(defn handler [{{url :url} :body}]
  
  (prn "trying to archive" url)

  (let [doi (scraper/take-screenshot! url)]
      {:body {:doi doi
              :resources (fetch-resources datastore/xtdb-node)}}))

#_{:clj-kondo/ignore [:unresolved-var]}
(defn get-resource-handler
  [req]
  (let [doi (:doi (:route-params req))]
    {:body (fetch-resource datastore/xtdb-node doi)}))