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

(defn- request-archival! [doi url description]
  (let [result (http/do-post scraper-api-url {:url url 
                                              :target doi
                                              :existingDescription description})]
    (prn "result" result)
    result))

(defn- rewrite-url 
  "This is a hack to account for that we route traffic within the docker compose network"
  [url]
  (str/replace url "widget-host:3000" "localhost:8022"))

(defn- save-resource! [doi url description]
  (datastore/upsert {:description description}
                    (rewrite-url url)
                    doi))

(defn archive! [url]
  (let [{existing-description :description
         existing-doi         :version ;; <- should be phrased in domain terms, i.e. doi, here
         :as _existing-entity} (datastore/get-by-id (rewrite-url url))
        existing-description (or existing-description "")
        doi (doi/generate)
        {:keys [description]} (request-archival! doi url existing-description)]

    (if 
     ;; TODO deduplicate with condition in Python code
     (and (= existing-description description)
          (not= "" existing-description)
          (not= "" description))
      existing-doi
      (do
        (register-doi! doi)
        (save-resource! doi url description)
        doi))))
