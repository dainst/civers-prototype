(ns api.widget
  (:require [ring.util.response :as response]
            [api.scraping :as scraping]))

(defn get-form [req]
  (let [referrer (get (:query-params req) "referrer")
        doi      (get (:query-params req) "doi")]
    
    (if doi
      (format "<div style=\"background-color: darkred; color: white\">
      <h1>Citator Widget</h1>
      <b>Cite this entity with as: %s (DOI)</b> 
      <p>Visit the DOI Registrar and search for this DOI do find 
         the archived snapshot of this page</div>" doi)
      
      (format "<div style=\"background-color: darkred; color: white\">
      <h1>Citator Widget</h1>
               <p>Cite this resource.<p/>
      <p> Hit the submit button to 
               take a snapshot and generate a DOI for this site</p>
      <form method=\"get\" action=\"/widget/request-archival\">
       <input id=\"hidden-field\"
              type=\"hidden\" 
              name=\"%s\">
       <input type=\"submit\" value=\"submit\"/>
      </form>
    </div>" referrer))))

(defn submit-handler [req]
  (let [referrer (first (keys (:query-params req)))
        path     (str "/widget?referrer=" (java.net.URLEncoder/encode referrer))]
    (let [doi (scraping/archive! referrer)]
      (response/redirect (str path "&doi=" doi)))))
