(ns resource
  (:require [reagent.core :as r]
            api))

(defn- doi-link [doi]
  [:a {:href   (str "/resource/" doi)
       :target "_blank"}
   doi])

(defn- date-component [resource-version]
  [:<>
   " ("
   (:date resource-version)
   ")"])

(defn- version-item [{:keys [doi date]}]
  [:li
   {:key doi}
   (doi-link doi)
   (date-component date)])

(defn- other-versions-component [resource-versions]
  [:table
   [:tbody
    [:tr
     [:td [:b "Other versions"]]
     [:td [:ul (map version-item resource-versions)]]]]])

(defn- metadata-component [resource versions resource-version-id]
  [:table
   [:tbody
    [:tr
     [:td [:b "DOI"]]
     [:td (:doi resource)]]
    [:tr
     [:td [:b "Archival date"]]
     [:td (:date resource)]]
    [:tr
     [:td [:b "Archived Site"]]
     [:td [:a {:href (str "/archive/" resource-version-id "/index.html")
               :target "_blank"} (str "/archive/" resource-version-id)]]]
    [:tr
     [:td [:b "Original URL"]]
     [:td [:a {:href (:url resource)
               :target "_blank"} (:url resource)]]]
    (when (seq versions)
      [other-versions-component])]])

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
         [metadata-component resource versions path]]))))
