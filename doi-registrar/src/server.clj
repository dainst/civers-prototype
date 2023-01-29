(ns server
  (:require [ring.adapter.undertow :refer [run-undertow]]
            [ring.adapter.undertow.websocket :as ws]
            [ring.util.response :as response]
            [compojure.core :refer [defroutes GET POST]]
            [ring.middleware.json :as json]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.reload :as reload]
            [mount.core :as mount]
            api))

(defonce chan (atom nil))

(defn wrap-api [handler]
  (-> handler
      json/wrap-json-response
      (json/wrap-json-body {:keywords? true})))

(defn wrap-send-ws [handler]
  (fn [req]
    (if @chan
      (ws/send "" @chan)
      (prn "websocket not open"))
    (handler req)))

(defroutes routes
  (GET "/api" [] (wrap-api api/get-handler))
  (POST "/api" [] (-> api/submit-handler
                      wrap-send-ws
                      wrap-api))
  (GET "/" [] (response/resource-response "public/index.html")))

(defn ws-handler [_request]
  {:undertow/websocket
   {:on-open (fn [{:keys [channel]}]
               (reset! chan channel)
               (println "WS open!" channel))
    :on-message (fn [{:keys [channel data]}]
                  (prn "on-message" data)
                  (ws/send data channel))
    :on-close   (fn [{:keys [_channel _ws-channel]}] (println "WS closed!"))}})

(defn wrap-ws [handler]
  (fn [req]
    (if (= "/ws" (:uri req))
      (ws-handler req)
      (handler req))))

(def app
  (-> #'routes
      wrap-ws
      reload/wrap-reload
      (wrap-resource "public")))

#_{:clj-kondo/ignore [:unresolved-symbol]}
(mount/defstate ^{:on-reload :noop} http-server
  :start
  (future (run-undertow app {:port 3000
                             ;; for docker compose env
                             :host "0.0.0.0"}))
  :stop 0)

(defn -main
  [& _args]
  (prn (mount/start))
  (.addShutdownHook (Runtime/getRuntime) (Thread. #(prn (mount/stop))))
  #_{:clj-kondo/ignore [:unresolved-symbol]}
  (deref http-server))