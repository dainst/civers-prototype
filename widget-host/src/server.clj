(ns server
  (:require [ring.adapter.jetty :as j]
            [compojure.core :refer [defroutes GET]]
            [ring.middleware.resource :refer [wrap-resource]]))

;; TODO use cljstache?

(defn- get-form 
  [title]
  (fn
    [_req]
    (let [img (if (= "a" title) 
                "<a href=\"https://commons.wikimedia.org/wiki/File:V%C3%BDkop%C3%A1vky_u_%C5%BDelenic.jpg\"
                    target=\"_blank\">
                    <img src=\"/640px-Výkopávky_u_Želenic.jpg\" height=\"320\"></a>"
                "<a href=\"https://commons.wikimedia.org/wiki/File:Arrizala_-_Sorgi%C3%B1etxe_04.jpg\"
                    target=\"_blank\">
                    <img src=\"/Arrizala_-_Sorgiñetxe_04.jpg\" height=\"320\"></a>")]
      (str "<head>
            <title>Widget Host</title>
            <link rel=\"stylesheet\" href=\"/main.css\">
          <head>
          <div>
         <h1>Widget Host</h1>
          <a href=\"/\">Home</a>
          <h2>Resource: "
           title
           "</h2>
      <iframe src=\"http://localhost:8021/widget?referrer=" 
           (java.net.URLEncoder/encode (str "http://widget-host:3000/" title)) " \" 
              title=\"Widget\"
              height=\"150\"
              width=\"400\"
              param1=\"value1\">
      </iframe>
      <br>" img
           "</div>"))))

(defn- main-page
  [_req]
  "<head>
       <title>Widget Host</title>
       <link rel=\"stylesheet\" href=\"/main.css\">
   <head>
   <h1>Widget Host</h1>
   <br>
   <a href=\"/a\">Detail View for Resource \"a\"</a>
   <br>
   <a href=\"/b\">Detail View for Resource \"b\"</a>
   ")

(defroutes routes
  (GET "/" [] main-page)
  (GET "/a" [] (get-form "a"))
  (GET "/b" [] (get-form "b")))

(def app 
  (-> routes
      (wrap-resource "public")))

(defn -main
  [& _args]
  (future (j/run-jetty app {:port 3000})))
