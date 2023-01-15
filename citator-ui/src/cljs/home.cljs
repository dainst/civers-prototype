(ns home
  (:require [reagent.core :as r]
            [ajax.core :refer [POST]]))

(defn atom-input [value]
  [:input {:type "text"
           :value @value
           :on-change #(reset! value (-> % .-target .-value))}])

(defn fetch [url generated-handle]
  (POST "/api" {:body (.stringify js/JSON (clj->js {:url url}))
                :headers {"Content-Type" "application/json"}
                :handler (fn [resp]

                           (reset! generated-handle (get resp "doi")))
                :error-handler (fn [resp] (prn "Error response:" resp))}))

(defn component []
  (let [url (r/atom "")
        generated-handle (r/atom "")]
    (fn []
      [:<>
       [:h1 "Data Citation Service"]
       [:p "Insert a url here and submit"]
       [atom-input url]
       [:input {:type :button
                :on-click #(fetch @url generated-handle)
                :value "submit"}]
       (when-not (= "" @generated-handle)
         [:p "Generated handle: "
          [:a {:href (str "/resource/" @generated-handle)}
           @generated-handle]])])))
