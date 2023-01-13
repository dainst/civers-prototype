(ns browser
  (:require [ajax.core :refer [POST]]
            [reagent.core :as r]
            ["react-dom/client" :refer [createRoot]]
            [goog.dom :as gdom]))

(defonce root (createRoot (gdom/getElement "app")))

(defn fetch [url generated-handle]
  (POST "/api" {:body (.stringify js/JSON (clj->js {:url url}))
                :headers {"Content-Type" "application/json"}
                :handler (fn [resp] 
                           
                           (reset! generated-handle (get resp "doi"))
                           
                           )
                :error-handler (fn [resp] (prn "Error response:" resp))}))

(defn atom-input [value]
  [:input {:type "text"
           :value @value
           :on-change #(reset! value (-> % .-target .-value))}])

(defn main-component []
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
          [:a {:href (str "/storage/" @generated-handle ".png")}
           @generated-handle]])])))

(defn init
  []
  (.render root (r/as-element [main-component])))

;; start is called by init and after code reloading finishes
(defn ^:dev/after-load start []
  (init))

;; this is called before any code is reloaded
(defn ^:dev/before-load stop []
  (js/console.log "stop"))
