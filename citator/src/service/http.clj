(ns service.http
  (:require [clj-http.client :as client]
            [cheshire.core :as cheshire]))

(defn do-post [url json-body]
  (:body (client/post url {:body             (cheshire/generate-string json-body)
                           :as               :json
                           :content-type     :json
                           :accept           :json
                           :throw-exceptions false})))
