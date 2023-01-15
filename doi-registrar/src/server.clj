(ns server
  (:require [ring.adapter.jetty :as j]
            [ring.util.response :as response]
            [compojure.core :refer [defroutes GET POST]]
            [ring.middleware.json :as json]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.reload :as reload]
            [mount.core :as mount]
            api))

(defn wrap-api [handler]
  (-> handler
      json/wrap-json-response
      (json/wrap-json-body {:keywords? true})))

(defroutes routes
  (GET "/api" [] (wrap-api api/get-handler))
  (POST "/api" [] (wrap-api api/handler))
  (GET "/" [] (response/resource-response "public/index.html")))

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