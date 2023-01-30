(ns browser
  (:require [reagent.core :as r]
            ["react-dom/client" :refer [createRoot]]
            [goog.dom :as gdom]
            resources
            api))

(defonce root (createRoot (gdom/getElement "app")))

(defn- on-message [*resources]
  #(api/fetch-resources *resources))

(defn- create-ws [*resources]
  (let [ws  (js/WebSocket. "ws://localhost:3006/ws")]
    (set! (.-onopen ws) #(prn "onopen" (js/console.log %)))
    (set! (.-onerror ws) #(prn "onerror" (js/console.log %)))
    (set! (.-onclose ws) #(prn "onclose" (js/console.log %)))
    (set! (.-onmessage ws) (on-message *resources))))

(defn- main-component []
  (let [*resources (r/atom '())]
    (api/fetch-resources *resources)
    (create-ws *resources)
    (fn []
      [:<>
       [:h1 "DOI Registrar"]
       [resources/component @*resources]])))

(defn init
  []
  (.render root (r/as-element [main-component])))

;; start is called by init and after code reloading finishes
(defn ^:dev/after-load start []
  (init))

;; this is called before any code is reloaded
(defn ^:dev/before-load stop []
  (js/console.log "stop"))
