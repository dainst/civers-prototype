(ns resources)

(defn component [resources]
  [:<>
   [:hr]
   [:h2 "Resources"]
   [:table
    [:thead
     [:tr
      [:th "Registration date"]
      [:th "DOI"]
      [:th "Permalink to cited resources"]]]
    [:tbody
     (map (fn [{url "url" doi "doi" date "date"}]
            [:tr {:key doi}
             [:td date]
             [:td doi]
             [:td [:a {:href   url
                       :target "_blank"} url]]]) resources)]]])
