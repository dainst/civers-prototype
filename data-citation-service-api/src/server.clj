(ns server
  (:require [clojure.java.io :as io]
            [ring.adapter.jetty :as j]
            [ring.util.response :as response]
            [compojure.core :refer [defroutes GET POST]]
            [ring.middleware.json :as json]
            [ring.middleware.resource :refer [wrap-resource]]))

(defn api-handler [{{msg :msg} :body}]
  {:body {:echo msg}})

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
  (j/run-jetty app {:port 3000}))
