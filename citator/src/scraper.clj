(ns scraper
  (:require [clojure.string :as str]
            [clj-http.client :as client]
            [cheshire.core :as cheshire]
            [xtdb.api :as xt]
            datastore))

(defn- do-post [url json-body]
  (:body (client/post url {:body         (cheshire/generate-string json-body)
                           :as           :json
                           :content-type :json
                           :accept       :json})))

(defn- gen-doi []
  (subs (.toString (java.util.UUID/randomUUID)) 0 8))

(defn- register-doi! [doi]
  (do-post "http://doi-registrar:3000/api"
           {:doi doi
            :url (str "http://localhost:8020/resource/" doi)}))

(defn- request-screenshot! [url doi]
  (prn "status from take screenshot"
       (:status
        (do-post "http://scraper:5000/api/take-screenshot" {:url url :target doi}))))

#_{:clj-kondo/ignore [:unresolved-var]}
(defn- save-resource! [url doi]
  (xt/submit-tx datastore/xtdb-node 
                [[::xt/put
                  {:xt/id     doi
                   ;; This is a hack to account for that we route traffic within the docker compose network
                   :url       (str/replace url "widget-host:3000" "localhost:8022")
                   :user/name "citator"}]])
  (xt/sync datastore/xtdb-node))

(defn take-screenshot! [url]
  (let [doi (gen-doi)]

    (register-doi! doi)
    (request-screenshot! url doi)
    (save-resource! url doi)
    
    doi))