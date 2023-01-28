(ns browser
  (:require [clojure.string :as str]
            [reagent.core :as r]
            ["react-dom/client" :refer [createRoot]]
            [goog.dom :as gdom]
            home
            resource))

(defonce root (createRoot (gdom/getElement "app")))

(defn- main-component [path] 
  (case path
    "/"
    [home/component]
    [resource/component path]))

(defn- get-path []
  (str/replace (-> js/window .-location .-pathname) "/resource/" ""))

(defn init []
  (.render root (r/as-element [main-component (get-path)])))

(defn ^:dev/after-load start [] (init))

(defn ^:dev/before-load stop [] (js/console.log "stop"))
