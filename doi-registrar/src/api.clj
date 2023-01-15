(ns api
  (:require [xtdb.api :as xt]
            datastore))

#_{:clj-kondo/ignore [:unresolved-var]}
(defn handler [{{doi :doi
                 url :url} :body}]
  (prn [:insert :doi doi :url url])
  (xt/submit-tx datastore/xtdb-node [[::xt/put
                                      {:xt/id     doi
                                       :url       url
                                       :user/name "doi-registrar"}]])

  (xt/sync datastore/xtdb-node)
  {:body {:status :ok}})

(defn get-handler [_]
  (let [resources (xt/q
                   #_{:clj-kondo/ignore [:unresolved-var]}
                   (xt/db datastore/xtdb-node)
                   '{:find  [e url]
                     :where [[e :user/name "doi-registrar"]
                             [e :url url]]})
        resources (map (fn [[doi url]] {:doi doi
                                        :url url}) resources)]
    (prn "resources" resources)
    {:body {:resources resources}}))
