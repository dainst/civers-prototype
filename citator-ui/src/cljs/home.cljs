(ns home
  (:require [reagent.core :as r]
            [ajax.core :refer [POST]]))

(defn atom-input [value]
  [:input {:type "text"
           :value @value
           :on-change #(reset! value (-> % .-target .-value))}])

(defn fetch [url generated-handle resources]
  (POST "/api" {:body (.stringify js/JSON (clj->js {:url url}))
                :headers {"Content-Type" "application/json"}
                :handler (fn [resp] 
                           (let [doi (get resp "doi")
                                 resources* (get resp "resources")]
                             (reset! generated-handle doi)
                             (reset! resources resources*)))
                :error-handler (fn [resp] (prn "Error response:" resp))}))

;; TODO extract

(defn resources-component [_resources]
  (fn [resources]
    [:ul 
     (map (fn [{url "url" doi "doi"}]
            [:li {:key doi}
             [:a {:href (str "/resource/" doi)} doi] (str "  " url)]
            ) @resources)]))

;; TODO implement active search, use r/let to make a fetch call
(defn component []
  (let [url (r/atom "")
        generated-handle (r/atom "")
        resources (r/atom '())]
    (fn []
      (prn @resources)
      [:<>
       [:h1 "Data Citation Service"]
       [:p "Insert a url here and submit"]
       [atom-input url]
       [:input {:type :button
                :on-click #(fetch @url generated-handle resources)
                :value "submit"}]
       (when-not (= "" @generated-handle)
         [:p "Generated handle: "
          [:a {:href (str "/resource/" @generated-handle)}
           @generated-handle]])
       [resources-component resources]])))
