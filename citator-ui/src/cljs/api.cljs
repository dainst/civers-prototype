(ns api
  (:require [ajax.core :refer [GET POST]]))

(defn- stringify-map [m]
  ;; TODO is this clj->js even necessary?
  (.stringify js/JSON (clj->js m)))

(defn- make-call 
  ([method url handler] (make-call method url nil handler))
  ([method url body handler]
   (method url {:headers       {"Content-Type" "application/json"}
                :handler       handler
                :body          body
                :error-handler #(prn "Error response:" %)})))

;; TODO maybe this should be more abstract and not touch the *resources directly?
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
              (stringify-map {:url url})
              ;; TODO verify everything is fine, by checking for status ok in return body
              #(fetch-resources *resources f))))

;; TODO use make-call
(defn fetch-resource [resource path]
  (GET (str "/api/resource/" path)
    {:headers       {"Content-Type" "application/json"}
     :handler       #(reset! resource %)
     :error-handler (fn [resp] (prn "Error response:" resp))}))
