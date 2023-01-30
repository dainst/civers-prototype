(ns api
  (:require [ajax.core :refer [GET]]))

(defn fetch-resources [*resources]
  (GET "/api" {:body          (.stringify js/JSON (clj->js {:msg "hi"}))
               :headers       {"Content-Type" "application/json"}
               :handler       #(reset! *resources (get % "resources"))
               :error-handler (fn [resp] (prn "Error response:" resp))}))
