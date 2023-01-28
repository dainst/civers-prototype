(ns widget
  (:require [ring.util.response :as response]
            scraper))

(defn get-form [req]
  (let [referrer (get (:query-params req) "referrer")]
    (str "<div style=\"background-color: darkgreen\">
      <p>Take a snapshot and generate a DOI for this site</p>
      <form method=\"get\" action=\"submit\">
       <input id=\"hidden-field\"
              type=\"hidden\" 
              name=\"" referrer "\">
       <input type=\"submit\" value\"submit\" />
      </form>
    </div>")))

(defn submit-handler [req]
  (let [referrer (first (keys (:query-params req))) ;; TODO review
        path     (str "/widget?referrer=" (java.net.URLEncoder/encode referrer))]
    (scraper/take-screenshot! referrer)
    (response/redirect path)))
