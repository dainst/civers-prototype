(ns api
  (:require [clojure.walk :as walk]
            [ajax.core :refer [GET POST]]))

(def ^:private api-path "/api/")

(defn- stringify-map [m]
  ;; TODO is this clj->js even necessary?
  (.stringify js/JSON (clj->js m)))

(defn- make-call 
  ([method path handler] (make-call method path nil handler))
  ([method path body handler]
   (method (str api-path path)
           {:headers       {"Content-Type" "application/json"}
            :handler       handler
            :body          body
            :error-handler #(prn "Error response:" %)})))

;; TODO maybe this should be more abstract and not touch the *resources directly?
(defn fetch-resources 
  ([*resources] (fetch-resources *resources nil))
  ([*resources f]
   (make-call GET "resources"
              #(do (reset! *resources (get % "resources"))
                   (when f (f))))))

(defn archive-url!
  ([url *resources] (archive-url! url *resources nil))
  ([url *resources f]
   ;; TODO improve on post and get /api, perhaps by having different endpoints
   (make-call POST 
              "resource" 
              (stringify-map {:url url})
              ;; TODO verify everything is fine, by checking for status ok in return body
              #(fetch-resources *resources f))))

(defn fetch-resource [resource path]
  (make-call GET
             (str "resource/" path)
             #(reset! resource (walk/keywordize-keys %))))
