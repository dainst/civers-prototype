(ns api
  (:require [ajax.core :refer [GET POST]]))

(defn- make-call 
  ([method url handler] (make-call method url nil handler))
  ([method url body handler]
   (method url {:headers       {"Content-Type" "application/json"}
                :handler       handler
                :body          body
                :error-handler (fn [resp] 
                                 (prn "Error response:" resp))})))

(defn fetch-resources 
  ([*resources] (fetch-resources *resources nil))
  ([*resources f]
   (make-call GET "/api"
              #(do (reset! *resources (get % "resources"))
                   (when f (f))))))

(defn archive-url!
  ([url *resources] (archive-url! url *resources nil))
  ([url *resources f]
   ;; TODO improve on post and get /api, perhaps by having different endpoints
   (make-call POST 
              "/api" 
              ;; TODO verify everything is fine
              (.stringify js/JSON (clj->js {:url url}))
              #(fetch-resources *resources f))))
