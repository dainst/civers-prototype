(ns scraper
  (:require [clojure.string :as str]
            doi
            http
            datastore))

(def ^:private doi-registrar-api-url "http://doi-registrar:3000/api")

(def ^:private scraper-api-url "http://scraper:5000/api/archive")

(defn- get-resource-url [doi]
  (str "http://localhost:8020/resource/" doi))

(defn- register-doi! [doi]
  (http/do-post doi-registrar-api-url
                {:doi doi
                 :url (get-resource-url doi)}))

(defn- request-archival! [doi url]
  (prn "status from take screenshot"
       (:status
        (http/do-post scraper-api-url {:url url :target doi}))))

(defn- rewrite-url 
  "This is a hack to account for that we route traffic within the docker compose network"
  [url]
  (str/replace url "widget-host:3000" "localhost:8022"))

(defn- make-resource [doi url]
  {:url       (rewrite-url url)
   :doi       doi
   :date      (java.util.Date.)})

(defn- save-resource! [node doi url]
  (datastore/create node
                    (make-resource doi url)
                    doi))

(defn archive! [url]
  (let [doi (doi/generate)]

    (register-doi! doi)
    (request-archival! doi url)
    #_{:clj-kondo/ignore [:unresolved-var]}
    (save-resource! datastore/xtdb-node doi url)
    
    doi))