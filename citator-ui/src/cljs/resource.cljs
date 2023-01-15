(ns resource)

(defn component [path]
  [:<> [:h1 path]
   [:a {:href (str "/archive/" path ".png")} "screenshot"]])
