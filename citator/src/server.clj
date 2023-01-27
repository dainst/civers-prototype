(ns server
  (:require [clojure.string :as str]
            [ring.adapter.jetty :as j]
            [ring.util.response :as response]
            [compojure.core :refer [defroutes GET POST]]
            [ring.middleware.json :as json]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.reload :as reload]
            [mount.core :as mount]
            api))

(defn- wrap-api [handler]
  (-> handler
      json/wrap-json-response
      (json/wrap-json-body {:keywords? true})))

(defn- get-form [_req]
  (prn "get-form")
  "<div style=\"background-color: darkgreen\">
      <p>Take a snapshot and generate a DOI for this site</p>
      <form method=\"get\" action=\"submit\">
       <input type=\"hidden\" 
              name=\"value\" 
              value=\"http://localhost:8022\"/>
       <input type=\"submit\" value\"submit\" />
      </form>
    </div>")

(defn- submit-handler [req]
  (prn "req" (java.net.URLDecoder/decode (str/replace (:query-string req) "value=" "")))
  {:status 200}
  (response/redirect "/widget"))

(defroutes routes
  (GET "/api" [] (wrap-api api/get-handler))
  (POST "/api" [] (wrap-api api/handler))
  (GET "/resource/:id" [] (response/resource-response "public/index.html"))
  (GET "/" [] (response/resource-response "public/index.html"))
  (GET "/widget" [] get-form)
  (GET "/submit" [] submit-handler))

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
