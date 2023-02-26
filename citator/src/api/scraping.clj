(ns api.scraping
  (:require [clojure.string :as str]
            [service.doi :as doi]
            [service.http :as http]
            [service.datastore :as datastore]))

(def ^:private doi-registrar-api-url "http://doi-registrar:3000/api")

(def ^:private scraper-api-url "http://scraper:5000/api/archive")

(defn- get-resource-url [doi]
  (str "http://localhost:8021/resource/" doi))

(defn- register-doi! [doi]
  (http/do-post doi-registrar-api-url
                {:doi doi
                 :url (get-resource-url doi)}))

(defn- request-archival! [doi url last-updated]
  (http/do-post scraper-api-url {:url url 
                                 :target doi
                                 :existingLastUpdated last-updated}))

(defn- rewrite-url 
  "This is a hack to account for that we route traffic within the docker compose network"
  [url]
  (str/replace url "widget-host:3000" "localhost:8022"))

(defn- save-resource! [doi url last-updated]
  (datastore/upsert {:last-updated last-updated}
                    (rewrite-url url)
                    doi))

(defn archive! [url]
  (let [{existing-last-updated :last-updated
         existing-doi          :version ;; <- TODO should be phrased in domain terms, i.e. doi, here
         :as _existing-entity} (datastore/get-by-id (rewrite-url url))
        existing-last-updated (or existing-last-updated "")
        doi (doi/generate)
        {last-updated :lastUpdated} (request-archival! doi url existing-last-updated)]

    (prn "last updated" last-updated existing-last-updated)
    (if 
     ;; TODO deduplicate with condition in Python code
     (and (= existing-last-updated last-updated)
          (not= "" existing-last-updated)
          (not= "" last-updated))
      existing-doi
      (do
        (register-doi! doi)
        (save-resource! doi url last-updated)
        doi))))
