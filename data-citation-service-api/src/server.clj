(ns server
  (:require [clj-http.client :as client]
            [cheshire.core :as cheshire]
            [reloader.core :as reloader]
            [ring.adapter.jetty :as j]
            [ring.util.response :as response]
            [compojure.core :refer [defroutes GET POST]]
            [ring.middleware.json :as json]
            [ring.middleware.resource :refer [wrap-resource]]))

(defn- do-post [url json-body]
  (:body (client/post url {:body         (cheshire/generate-string json-body)
                           :as           :json
                           :content-type :json
                           :accept       :json})))

(defn api-handler [{{url :url} :body}]
  (let [doi "test123"
        doi
        (:doi 
         (do-post "http://doi-service-api:3000/api" {:msg doi}))]
    
    (prn "status from take screenshot" 
         (:status
          (do-post "http://scraping-service-api:5000/api/take-screenshot" {:url url :target doi})))
    
    {:body {:doi doi}}))

(defn wrap-api [handler]
  (-> handler
      json/wrap-json-response
      (json/wrap-json-body {:keywords? true})))

(defroutes routes
  (POST "/api" [] (wrap-api api-handler))
  (GET "/" [] (response/resource-response "public/index.html")))

(def app
  (-> routes
      (wrap-resource "public")))

(defn -main
  [& _args]
  (reloader/start ["src"])
  (j/run-jetty #'app {:port 3000}))
