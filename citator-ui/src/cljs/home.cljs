(ns home
  (:require [reagent.core :as r]
            [home.resources :as resources]
            api))

(defn on-message [*resources]
  #(api/fetch-resources *resources))

(defn- create-ws [*resources]
  (let [ws  (js/WebSocket. "ws://localhost:3005/ws")]
    (set! (.-onopen ws) #(prn "onopen" (js/console.log %)))
    (set! (.-onerror ws) #(prn "onerror" (js/console.log %)))
    (set! (.-onclose ws) #(prn "onclose" (js/console.log %)))
    (set! (.-onmessage ws) (on-message *resources))))

(defn- atom-input [value]
  [:input#text-input.text {:type        "text"
                           :value       @value
                           :placeholder "http(s)://domain-name.(com|org|net|...)/path/to/resource"
                           :on-change   #(reset! value (-> % .-target .-value))}])

(defn- button [on-click-fn]
  [:input {:type     :button
           :on-click on-click-fn
           :value    "submit"}])

(defn- reset-input [_]
  (set! (.-value (js/document.getElementById "text-input")) ""))

(defn- main-component [*url]
  [:<> [:h1 "Citator"]
   [:p "Insert the URL of a site you want to archive here and click submit."]
   [atom-input *url]
   [button #(api/archive-url! @*url reset-input)]])

;; TODO use r/let to make a fetch call
(defn component []
  (let [*url       (r/atom "")
        *resources (r/atom '())]
    (api/fetch-resources *resources)
    (create-ws *resources)
    (fn []
      [:<>
       [main-component *url]
       (when (seq @*resources)
         [resources/component @*resources])])))
