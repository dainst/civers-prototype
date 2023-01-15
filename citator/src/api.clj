(ns api
  (:require [clj-http.client :as client]
            [cheshire.core :as cheshire]))

(defn- do-post [url json-body]
  (:body (client/post url {:body         (cheshire/generate-string json-body)
                           :as           :json
                           :content-type :json
                           :accept       :json})))

(defn handler [{{url :url} :body}]
  (let [doi "test123"
        doi
        (:doi
         (do-post "http://doi-registrar:3000/api" {:msg doi}))]

    (prn "status from take screenshot"
         (:status
          (do-post "http://scraper:5000/api/take-screenshot" {:url url :target doi})))

    {:body {:doi doi}}))
