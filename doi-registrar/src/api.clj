(ns api
  (:require [xtdb.api :as xt]
            datastore))

(defn- format-date [date]
  (.format (java.text.SimpleDateFormat. "yyyy-MM-dd HH:mm") date))

(defn- make-resource [doi url]
  {;; This is a hack to account for that we route traffic within the docker compose network
   :url       url
   :doi       doi
   :date      (java.util.Date.)})

(defn- store [node resource id]
  (xt/submit-tx node [[::xt/put
                       (assoc resource :xt/id id)]])
  (xt/sync node))

(defn- register-resource! [node doi url]
  (store node (make-resource doi url) doi))

(defn- fetch-resources [node]
  (let [resources (map first (xt/q
                              (xt/db node)
                              '{:find     [(pull ?e [*]) date]
                                :where    [[?e :date date]]
                                :order-by [[date :desc]]}))]

    (map #(update % :date format-date) resources)))

(defn handler [{{doi :doi
                 url :url} :body}]
  (prn [:insert :doi doi :url url])
  #_{:clj-kondo/ignore [:unresolved-var]}
  (register-resource! datastore/xtdb-node doi url)
  {:body {:status :ok}})

(defn get-handler [_]
  (let [resources 
        #_{:clj-kondo/ignore [:unresolved-var]}
        (fetch-resources datastore/xtdb-node)]
    (prn "resources" resources)
    {:body {:resources resources}}))
