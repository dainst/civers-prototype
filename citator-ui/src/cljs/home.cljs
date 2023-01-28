(ns home
  (:require [reagent.core :as r]
            [ajax.core :refer [GET POST]]))

(defn atom-input [value]
  [:input {:type "text"
           :value @value
           :on-change #(reset! value (-> % .-target .-value))}])

(defn archive-url! [url generated-handle resources]
  (POST "/api" {:body (.stringify js/JSON (clj->js {:url url}))
                :headers {"Content-Type" "application/json"}
                :handler (fn [resp] 
                           (let [doi (get resp "doi")
                                 resources* (get resp "resources")]
                             (reset! generated-handle doi)
                             (reset! resources resources*)))
                :error-handler (fn [resp] (prn "Error response:" resp))}))

(defn fetch-resources [resources]
  (GET "/api" {:headers       {"Content-Type" "application/json"}
               :handler       (fn [resp]
                                (let [resources* (get resp "resources")]
                                  (reset! resources resources*)))
               :error-handler (fn [resp] (prn "Error response:" resp))}))

(defn resources-component [_resources]
  (fn [resources]
    [:<>
     [:hr]
     [:h2 "Resources"]
     [:ul 
      (map (fn [{url "url" doi "doi" date "date"}]
             [:li {:key doi}
              date " "
              [:a {:href (str "/resource/" doi)} doi] 
              " "
              [:a {:href url} url]]
             ) @resources)]]))

;; TODO implement active search, use r/let to make a fetch call
(defn component []
  (let [url (r/atom "")
        generated-handle (r/atom "")
        resources (r/atom '())]
    (fetch-resources resources)
    (fn []
      [:<>
       [:h1 "Citator"]
       [:p "Insert a url here and submit"]
       [atom-input url]
       [:input {:type :button
                :on-click #(archive-url! @url generated-handle resources)
                :value "submit"}]
       (when-not (= "" @generated-handle)
         [:p "Generated handle: "
          [:a {:href (str "/resource/" @generated-handle)}
           @generated-handle]])
       [resources-component resources]])))
