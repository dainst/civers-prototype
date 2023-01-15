(ns server
  (:require [reloader.core :as reloader]
            [ring.adapter.jetty :as j]
            [ring.util.response :as response]
            [compojure.core :refer [defroutes GET POST]]
            [ring.middleware.json :as json]
            [ring.middleware.resource :refer [wrap-resource]]
            api))

(defn wrap-api [handler]
  (-> handler
      json/wrap-json-response
      (json/wrap-json-body {:keywords? true})))

(defroutes routes
  (GET "/api" [] (wrap-api api/get-handler))
  (POST "/api" [] (wrap-api api/handler))
  (GET "/resource/:id" [] (response/resource-response "public/index.html"))
  (GET "/" [] (response/resource-response "public/index.html")))

(def app
  (-> routes
      (wrap-resource "public")))

(defn -main
  [& _args]
  (reloader/start ["src"])
  (j/run-jetty #'app {:port 3000}))
