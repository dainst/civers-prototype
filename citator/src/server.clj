(ns server
  (:require [ring.adapter.jetty :as j]
            [ring.util.response :as response]
            [compojure.core :refer [defroutes GET POST]]
            [ring.middleware.json :as json]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.reload :as reload]
            [ring.middleware.params :refer [wrap-params]]
            [mount.core :as mount]
            api
            widget))

(defn- wrap-api [handler]
  (-> handler
      json/wrap-json-response
      (json/wrap-json-body {:keywords? true})))

(defroutes routes
  (GET "/api" [] (wrap-api api/get-handler))
  (POST "/api" [] (wrap-api api/handler))
  (GET "/resource/:id" [] (response/resource-response "public/index.html"))
  (GET "/" [] (response/resource-response "public/index.html"))
  (GET "/widget" [] (wrap-params widget/get-form))
  (GET "/submit" [] (wrap-params widget/submit-handler)))

(def app
  (-> routes
      reload/wrap-reload
      (wrap-resource "public")))

#_{:clj-kondo/ignore [:unresolved-symbol]}
(mount/defstate ^{:on-reload :noop} http-server
  :start
  (future (j/run-jetty app {:port 3000}))
  :stop 0)

(defn -main
  [& _args]
  (prn (mount/start))
  (.addShutdownHook (Runtime/getRuntime) (Thread. #(prn (mount/stop))))
  #_{:clj-kondo/ignore [:unresolved-symbol]}
  (deref http-server))
