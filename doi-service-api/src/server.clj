(ns server
  (:require [ring.adapter.jetty :as j]
            [compojure.core :refer [defroutes POST]]
            [ring.middleware.json :as json]))

(defn api-handler [{{msg :msg} :body}]
  {:body {:answer-from-doi-service msg}})

(defn wrap-api [handler]
  (-> handler
      json/wrap-json-response
      (json/wrap-json-body {:keywords? true})))

(defroutes routes
  (POST "/api" [] (wrap-api api-handler)))

(defn -main
  [& _args]
  (j/run-jetty routes {:port 3000}))
