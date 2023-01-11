(ns server
  (:require [clj-http.client :as client]
            [cheshire.core :as cheshire]
            [reloader.core :as reloader]
            [ring.adapter.jetty :as j]
            [ring.util.response :as response]
            [compojure.core :refer [defroutes GET POST]]
            [ring.middleware.json :as json]
            [ring.middleware.resource :refer [wrap-resource]]))

(defn api-handler [{{msg :msg} :body}]
  (prn (cheshire/parse-string (:body (client/post "http://doi-service-api:3000/api"
                                              {:body         "{\"msg\": \"hello\"}"
                                               :content-type :json
                                               :accept       :json}))))
  {:body {:echo (str msg "!")}})

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
