(ns home
  (:require [reagent.core :as r]
            [home.resources :as resources]
            api))

(defn atom-input [value]
  [:input.text {:type      "text"
                :value     @value
                :on-change #(reset! value (-> % .-target .-value))}])

(defn- create-ws [*resources]
  (let [ws  (js/WebSocket. "ws://localhost:3005/ws")]
    (set! (.-onopen ws)
          (fn [a] (prn "onopen" (js/console.log a))))
    (set! (.-onerror ws)
          (fn [a] (prn "onerror" (js/console.log a))))
    (set! (.-onclose ws)
          (fn [a] (prn "onclose" (js/console.log a))))
    (set! (.-onmessage ws)
          (fn [_a]
            #_(prn ".." (.-data a))
            (js/setTimeout 
             #(api/fetch-resources *resources) 
             ;; TODO review, this is necessary because of asynchronicity between citator backend and scraper              
             15000)))))

;; TODO use r/let to make a fetch call
(defn component []
  (let [*url       (r/atom "")
        reset-url! #(reset! *url "")
        *resources (r/atom '())]
    (api/fetch-resources *resources)
    (create-ws *resources)
    (fn []
      [:<>
       [:h1 "Citator"]
       [:p "Insert the URL of a site you want to archive here and click submit."]
       [atom-input *url]
       [:input {:type     :button
                :on-click #(api/archive-url! @*url *resources reset-url!)
                :value    "submit"}]
       [resources/component @*resources]])))
