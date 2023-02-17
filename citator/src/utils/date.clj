(ns utils.date)

(defn now []
  (java.util.Date.))

(defn format-date [date]
  (.format (java.text.SimpleDateFormat. "yyyy-MM-dd HH:mm") date))
