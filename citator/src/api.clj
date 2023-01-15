(ns api
  (:require [clj-http.client :as client]
            [cheshire.core :as cheshire]
            [xtdb.api :as xt]
            datastore))

(defn- do-post [url json-body]
  (:body (client/post url {:body         (cheshire/generate-string json-body)
                           :as           :json
                           :content-type :json
                           :accept       :json})))

(defn get-handler [_]
  (let [resources (xt/q (xt/db datastore/xtdb-node)
                        '{:find  [e url]
                          :where [[e :user/name "citator"]
                                  [e :url url]]})
        resources (map (fn [[doi url]] {:doi doi
                                        :url url}) resources)]
    {:body {:resources resources}}))

(defn handler [{{url :url} :body}]
  (let [doi (subs (.toString (java.util.UUID/randomUUID)) 0 8)
        doi
        (:doi
         (do-post "http://doi-registrar:3000/api" {:msg doi}))]
    
    (prn "status from take screenshot"
         (:status
          (do-post "http://scraper:5000/api/take-screenshot" {:url url :target doi})))

    (xt/submit-tx datastore/xtdb-node [[::xt/put
                                        {:xt/id     doi
                                         :url       url
                                         :user/name "citator"}]])
    
    (xt/sync datastore/xtdb-node)
    
    (let [resources (xt/q (xt/db datastore/xtdb-node) 
                          '{:find  [e url]
                            :where [[e :user/name "citator"]
                                    [e :url url]]})
          resources (map (fn [[doi url]] {:doi doi
                                         :url url}) resources)]
      {:body {:doi doi
              :resources resources}})))
