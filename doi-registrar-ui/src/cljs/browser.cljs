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
                                  (reset! resources resources*)))
               :error-handler (fn [resp] (prn "Error response:" resp))}))

(defn- create-ws [resources]
  (let [ws  (js/WebSocket. "ws://localhost:3006/ws")]
    (set! (.-onopen ws)
          (fn [a] (prn "onopen" (js/console.log a))))
    (set! (.-onerror ws)
          (fn [a] (prn "onerror" (js/console.log a))))
    (set! (.-onclose ws)
          (fn [a] (prn "onclose" (js/console.log a))))
    (set! (.-onmessage ws)
          (fn [_a]
            (js/setTimeout
             #(fetch resources)
             ;; TODO review, this is necessary because of asynchronicity between citator backend and scraper              
             3000)))))

(defn resources-component [_resources]
  (fn [resources]
    [:<>
     [:hr]
     [:h2 "Resources"]
     [:table
      [:thead
       [:tr 
        [:th "Registration date"] 
        [:th "DOI"] 
        [:th "URL"]]]
      [:tbody
       (map (fn [{url "url" doi "doi" date "date"}]
              [:tr {:key doi}
               [:td date]
               [:td doi]
               [:td [:a {:href   url
                         :target "_blank"} url]]]) @resources)]]]))

(defn- main-component []
  (let [resources (r/atom '())]
    (fetch resources)
    (create-ws resources)
    (fn []
      [:<>
       [:h1 "DOI Registrar"]
       [resources-component resources]])))

(defn init
  []
  (.render root (r/as-element [main-component])))

;; start is called by init and after code reloading finishes
(defn ^:dev/after-load start []
  (init))

;; this is called before any code is reloaded
(defn ^:dev/before-load stop []
  (js/console.log "stop"))
