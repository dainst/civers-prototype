(ns api
  (:require [ajax.core :refer [GET POST]]))

(defn- make-call 
  ([method url handler] (make-call method url nil handler))
  ([method url body handler]
   (method url {:headers       {"Content-Type" "application/json"}
                :handler       handler
                :body          body
                :error-handler #(prn "Error response:" %)})))

(defn fetch-resources 
  ([*resources] (fetch-resources *resources nil))
  ([*resources f]
   (make-call GET "/api/resources"
              #(do (reset! *resources (get % "resources"))
                   (when f (f))))))

(defn archive-url!
  ([url *resources] (archive-url! url *resources nil))
  ([url *resources f]
   ;; TODO improve on post and get /api, perhaps by having different endpoints
   (make-call POST 
              "/api/resource" 
              ;; TODO verify everything is fine
              (.stringify js/JSON (clj->js {:url url}))
              #(fetch-resources *resources f))))

;; TODO use make-call
(defn fetch-resource [resource path]
  (GET (str "/api/resource/" path)
    {:headers       {"Content-Type" "application/json"}
     :handler       #(reset! resource %)
     :error-handler (fn [resp] (prn "Error response:" resp))}))
