(ns server
  (:require [ring.adapter.jetty :as j]
            [compojure.core :refer [defroutes GET]]
            [ring.middleware.resource :refer [wrap-resource]]))

(defn- get-form [_req]
  (prn "get-form")
  "<div style=\"background-color: green\">
      <h1>Widget Host</h1>
      <iframe src=\"http://localhost:8020/widget\" 
              title=\"Widget\"
              height=\"400\"
              width=\"400\">
      </iframe>
    </div>")


(defroutes routes
  (GET "/" [] get-form))

(def app 
  (-> routes
      (wrap-resource "public")))

(defn -main
  [& _args]
  (future (j/run-jetty app {:port 3000})))
