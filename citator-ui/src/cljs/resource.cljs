(ns resource
  (:require [reagent.core :as r]
            [ajax.core :refer [GET]]))

(defn fetch-resource [resource path]
  (GET (str "/api/resource/" path)
    {:headers       {"Content-Type" "application/json"}
     :handler       (fn [resp]
                      (prn "this is what i got back: " resp)
                      (reset! resource resp))
     :error-handler (fn [resp] (prn "Error response:" resp))}))

(defn component [path]
  (let [resource (r/atom nil)]
    (fetch-resource resource path)
    (fn [_path]
      [:<> [:h1 path]
       [:br]
       (get @resource "date")
       [:br]
       [:a {:href (get @resource "url")} (get @resource "url")]
       [:br]
       [:a {:href (str "/archive/" path ".png")} "screenshot"]
       [:br]
       [:a {:href (str "/archive/" path "/index.html")} "archived-site"]])))
