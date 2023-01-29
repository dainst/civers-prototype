(ns service.doi)

(defn generate []
  (subs (.toString (java.util.UUID/randomUUID)) 0 8))
