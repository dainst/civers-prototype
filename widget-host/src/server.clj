(ns server
  (:require [clojure.java.io :as io]
            [ring.adapter.jetty :as j]
            [ring.util.response :as response]
            [compojure.core :refer [defroutes GET]]
            [ring.middleware.resource :refer [wrap-resource]]))

(def first-last-updated "2023-02-15")

(def second-last-updated "2023-02-16")

;; Mimicking a db
(def last-updated (atom first-last-updated))

(defn- make-img [title]
  (if (= "a" title)
    "<a href=\"https://commons.wikimedia.org/wiki/File:V%C3%BDkop%C3%A1vky_u_%C5%BDelenic.jpg\"
                    target=\"_blank\">
                    <img src=\"/640px-Výkopávky_u_Želenic.jpg\" height=\"320\"></a>"
    "<a href=\"https://commons.wikimedia.org/wiki/File:Arrizala_-_Sorgi%C3%B1etxe_04.jpg\"
                    target=\"_blank\">
                    <img src=\"/Arrizala_-_Sorgiñetxe_04.jpg\" height=\"320\"></a>"))

(defn- get-form-template [title url img last-updated]
  (format (slurp (io/resource "public/detail-view.html"))
          title
          url
          img
          last-updated))

(defn- detail-view
  [title]
  (fn
    [_req]
    (let [url (java.net.URLEncoder/encode 
               (str "http://widget-host:3000/" title))
          img (make-img title)]
      (get-form-template title url img @last-updated))))

(defn- main-page
  [_req]
  (slurp (io/resource "public/main.html")))

(defn- change-last-updated! []
  (reset! last-updated (if (= first-last-updated @last-updated)
                         second-last-updated
                         first-last-updated)))

(defn- change-last-updated [req]
  (change-last-updated!)
  (response/redirect (get-in req [:headers "referer"])))

(defroutes routes
  (GET "/" [] main-page)
  (GET "/a" [] (detail-view "a"))
  (GET "/b" [] (detail-view "b"))
  (GET "/change-last-updated" [] change-last-updated))

(def app 
  (-> routes
      (wrap-resource "public")))

(defn -main
  [& _args]
  (future (j/run-jetty app {:port 3000})))
