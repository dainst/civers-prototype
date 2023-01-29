(ns scraper
  (:require [clojure.string :as str]
            [clj-http.client :as client]
            [cheshire.core :as cheshire]
            [xtdb.api :as xt]
            datastore))

(defn- do-post [url json-body]
  (:body (client/post url {:body             (cheshire/generate-string json-body)
                           :as               :json
                           :content-type     :json
                           :accept           :json
                           :throw-exceptions false})))

(defn- gen-doi []
  (subs (.toString (java.util.UUID/randomUUID)) 0 8))

(defn- register-doi! [doi]
  (do-post "http://doi-registrar:3000/api"
           {:doi doi
            :url (str "http://localhost:8020/resource/" doi)}))

(defn- request-archival! [doi url]
  (prn "status from take screenshot"
       (:status
        (do-post "http://scraper:5000/api/archive" {:url url :target doi}))))

(defn- store [node resource id]
  (xt/submit-tx node
                [[::xt/put
                  (assoc resource :xt/id id)]])
  (xt/sync node))

(defn- make-resource [doi url]
  {;; This is a hack to account for that we route traffic within the docker compose network
   :url       (str/replace url "widget-host:3000" "localhost:8022")
   :doi       doi
   :date      (java.util.Date.)})

(defn- save-resource! [node doi url]
  (store node
         (make-resource doi url)
         doi))

(defn archive! [url]
  (let [doi (gen-doi)]

    (register-doi! doi)
    (request-archival! doi url)
    #_{:clj-kondo/ignore [:unresolved-var]}
    (save-resource! datastore/xtdb-node doi url)
    
    doi))