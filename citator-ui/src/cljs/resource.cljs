(ns resource
  (:require [reagent.core :as r]
            api))

(defn- doi-link [doi]
  [:a {:href   (str "/resource/" doi)
       :target "_blank"}
   doi])

(defn- archived-site-link [doi]
  [:a {:href (str "/archive/" doi "/index.html")
       :target "_blank"} (str "/archive/" doi)])

(defn- img [doi]
  [:a {:href (str "/archive/" doi ".png")
       :target "_blank"}
   [:img {:src    (str "/archive/" doi ".png")
          :height "400px"
          :width  :auto}]])

(defn- date-component [date]
  [:<> " (" date ")"])

(defn- version-item [{:keys [doi date]}]
  [:li
   {:key doi}
   (doi-link doi)
   (date-component date)])

(defn- other-versions-component [resource-versions]
  [:tr
   [:td [:b "Other versions"]]
   [:td [:ul (map version-item resource-versions)]]])

(defn- metadata-component [resource-version resource-versions doi]
  [:table
   [:tbody
    [:tr
     [:td [:b "DOI"]]
     [:td (:doi resource-version)]]
    [:tr
     [:td [:b "Archival date"]]
     [:td (:date resource-version)]]
    (when-not (empty? (:last-updated resource-version))
      [:tr
       [:td [:b "Last updated"]]
       [:td (:last-updated resource-version)]])
    [:tr
     [:td [:b "Archived Site"]]
     [:td (archived-site-link doi)]]
    [:tr
     [:td [:b "Original URL"]]
     [:td [:a {:href (:url resource-version)
               :target "_blank"} (:url resource-version)]]]
    (when (seq resource-versions)
      [other-versions-component resource-versions])]])

(defn component [doi]
  (let [*resource-version (r/atom nil)]
    (api/fetch-resource *resource-version doi)
    (fn [_path]
      (let [resource-version @*resource-version
            resource-versions (:versions resource-version)]
        [:<> [:h1 "Citator"]
         [:h2 "Detail view: " doi]
         (img doi)
         [metadata-component resource-version resource-versions doi]]))))
