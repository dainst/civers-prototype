(ns home.resources)

(defn component [resources]
  [:<>
   [:hr]
   [:h2 "Resources"]
   [:table
    [:thead
     [:tr
      [:th "Date"]
      [:th "DOI"]
      [:th "URL"]]]
    [:tbody
     (map (fn [{url  "url"
                doi  "doi"
                date "date"}]
            [:tr {:key doi}
             [:td date]
             [:td [:a {:href (str "/resource/" doi)
                       :target "_blank"} doi]]
             [:td [:a {:href url
                       :target "_blank"} url]]])
          resources)]]])
