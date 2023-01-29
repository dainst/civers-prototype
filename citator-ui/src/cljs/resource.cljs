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
      [:<> [:h1 "Citator"]
       [:h2 "Detail view: " path]
       [:a {:href (str "/archive/" path ".png")}
        [:img {:src    (str "/archive/" path ".png")
               :height "400px"
               :width  :auto}]]
       [:table
        [:tbody
         [:tr
          [:td [:b "DOI"]]
          [:td (get @resource "doi")]]
         [:tr
          [:td [:b "Archival date"]]
          [:td (get @resource "date")]]
         [:tr
          [:td [:b "Archived Site"]]
          [:td [:a {:href (str "/archive/" path "/index.html")} (str "/archive/" path)]]]
         [:tr
          [:td [:b "Original URL"]]
          [:td [:a {:href (get @resource "url")
                    :target "_blank"} (get @resource "url")]]]]]])))
