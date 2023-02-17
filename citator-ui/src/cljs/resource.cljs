(ns resource
  (:require [reagent.core :as r]
            api))

(defn component [path]
  (let [resource (r/atom nil)]
    (api/fetch-resource resource path)
    (fn [_path]
      (let [resource @resource
            versions (get resource "versions")]
        [:<> [:h1 "Citator"]
         [:h2 "Detail view: " path]
         [:a {:href (str "/archive/" path ".png")
              :target "_blank"}
          [:img {:src    (str "/archive/" path ".png")
                 :height "400px"
                 :width  :auto}]]
         [:table
          [:tbody
           [:tr
            [:td [:b "DOI"]]
            [:td (get resource "doi")]]
           [:tr
            [:td [:b "Archival date"]]
            [:td (get resource "date")]]
           [:tr
            [:td [:b "Archived Site"]]
            [:td [:a {:href (str "/archive/" path "/index.html")
                      :target "_blank"} (str "/archive/" path)]]]
           [:tr
            [:td [:b "Original URL"]]
            [:td [:a {:href (get resource "url")
                      :target "_blank"} (get resource "url")]]]
           (if (seq versions)
             [:tr
              [:td [:b "Other versions"]]
              [:td [:ul (map (fn [version]
                               (prn version)
                               (let [doi (get version "doi")]
                                 [:li
                                  {:key doi}
                                  [:a {:href   (str "/resource/" doi)
                                       :target "_blank"}
                                   doi]
                                  " ("
                                  (get version "date")
                                  ")"]))
                             versions)]]])]]]))))
