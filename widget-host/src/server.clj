(ns server
  (:require [ring.adapter.jetty :as j]
            [compojure.core :refer [defroutes GET]]
            [ring.middleware.resource :refer [wrap-resource]]))

(defn- get-form 
  [title]
  (fn
    [_req]
    (str "<div style=\"background-color: green\">
      <h1>Widget Host</h1>
          <h2>"
         title
         "</h2>
      <iframe src=\"http://localhost:8020/widget?referrer=" 
         (java.net.URLEncoder/encode (str "http://widget-host:3000/" (name title)))" \" 
              title=\"Widget\"
              height=\"400\"
              width=\"400\"
              param1=\"value1\">
      </iframe>
      <a href=\"/\">Back</a>
    </div>")))

(defn- main-page
  [_req]
  "<h1>Widget Host</h1>
   <br>
   <a href=\"/a\">Detail View for Resource \"a\"</a>
   <br>
   <a href=\"/b\">Detail View for Resource \"b\"</a>
   ")

(defroutes routes
  (GET "/" [] main-page)
  (GET "/a" [] (get-form :a))
  (GET "/b" [] (get-form :b)))

(def app 
  (-> routes
      (wrap-resource "public")))

(defn -main
  [& _args]
  (future (j/run-jetty app {:port 3000})))
