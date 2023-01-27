(ns scraper
  (:require [clj-http.client :as client]
            [cheshire.core :as cheshire]
            [xtdb.api :as xt]
            datastore))

(defn- do-post [url json-body]
  (:body (client/post url {:body         (cheshire/generate-string json-body)
                           :as           :json
                           :content-type :json
                           :accept       :json})))

#_{:clj-kondo/ignore [:unresolved-var]}
(defn take-screenshot [url]
  (let [doi (subs (.toString (java.util.UUID/randomUUID)) 0 8)
        _ (do-post "http://doi-registrar:3000/api" {:doi doi
                                                   ;; TODO review
                                                    :url (str "http://localhost:8020/resource/" doi)})]
    
    (prn "status from take screenshot"
         (:status
          (do-post "http://scraper:5000/api/take-screenshot" {:url url :target doi})))

    (xt/submit-tx datastore/xtdb-node [[::xt/put
                                        {:xt/id     doi
                                         :url       url
                                         :user/name "citator"}]])
    
    (xt/sync datastore/xtdb-node)
    doi))