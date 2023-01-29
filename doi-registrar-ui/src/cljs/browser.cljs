(ns browser
  (:require [ajax.core :refer [GET]]
            [reagent.core :as r]
            ["react-dom/client" :refer [createRoot]]
            [goog.dom :as gdom]))

(defonce root (createRoot (gdom/getElement "app")))

(defn fetch [resources]
  (GET "/api" {:body          (.stringify js/JSON (clj->js {:msg "hi"}))
               :headers       {"Content-Type" "application/json"}
               :handler       (fn [resp] 
                                (let [resources* (get resp "resources")]
                                  (reset! resources resources*)
                                  (prn "Response from backend:" resp)))
               :error-handler (fn [resp] (prn "Error response:" resp))}))

(defn resources-component [_resources]
  (fn [resources]
    [:<>
     [:hr]
     [:h2 "Resources"]
     [:table
      [:thead
       [:tr 
        [:th "DOI"] 
        [:th "URL"]]]
      [:tbody
       (map (fn [{url "url" doi "doi"}]
              [:tr {:key doi}
               [:td (str doi " ")]
               [:td [:a {:href   url
                         :target "_blank"} url]]]) @resources)]]]))

(defn simple-component []
  (let [resources (r/atom '())]
    (fetch resources)
    (fn []
      [:<>
       [:h1 "DOI Registrar"]
       [resources-component resources]])))

(defn init
  []
  (.render root (r/as-element [simple-component])))

;; start is called by init and after code reloading finishes
(defn ^:dev/after-load start []
  (init))

;; this is called before any code is reloaded
(defn ^:dev/before-load stop []
  (js/console.log "stop"))
