(ns api.resources
  (:require [service.datastore :as datastore]
            [api.scraping :as scraping]))

#_{:clj-kondo/ignore [:unresolved-var]}
(defn get-handler [_]
  {:body {:resources (datastore/get-all)}})

#_{:clj-kondo/ignore [:unresolved-var]}
(defn submit-handler [{{url :url} :body}]
  (let [doi (scraping/archive! url)]
    {:body {:status :ok
            :doi    doi}}))

(defn get-resource-handler
  [req]
  (let [doi (:doi (:route-params req))]
    {:body (datastore/get-by-id doi)}))
