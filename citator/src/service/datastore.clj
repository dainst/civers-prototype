(ns service.datastore
  (:require [clojure.java.io :as io]
            [mount.core :as mount]
            [xtdb.api :as xt]))

;; TODO move somewhere else?
(defn- format-date [date]
  (.format (java.text.SimpleDateFormat. "yyyy-MM-dd HH:mm") date))

(defn- update-date [entity]
  (update entity :date format-date))

;; TODO see above
(defn- now []
  (java.util.Date.))

(defn start-xtdb! []
  (letfn [(kv-store [dir]
            {:kv-store {:xtdb/module 'xtdb.rocksdb/->kv-store
                        :db-dir (io/file dir)
                        :sync? true}})]
    (xt/start-node
     {:xtdb/tx-log (kv-store "data/dev/tx-log")
      :xtdb/document-store (kv-store "data/dev/doc-store")
      :xtdb/index-store (kv-store "data/dev/index-store")})))

(defn stop-xtdb! [xtdb-node]
  (.close xtdb-node))

#_{:clj-kondo/ignore [:unresolved-symbol]}
(mount/defstate xtdb-node
  :start (do
           (tap> [:xtdb-node :up])
           (start-xtdb!))
  :stop (do
          (tap> [:xtdb-node :down])
          (stop-xtdb! xtdb-node)
          nil))

(defn- exists? [version]
  (-> (xt/q #_{:clj-kondo/ignore [:unresolved-symbol]}
       (xt/db xtdb-node)
            '{:find  [version]
              :in    [version]
              :where [[?e :xt/id version]]}
            version)
      seq
      boolean))

(defn- get-date [version]
  (-> (xt/q #_{:clj-kondo/ignore [:unresolved-symbol]}
       (xt/db xtdb-node)
            '{:find  [date]
              :in    [version]
              :where [[?e :xt/id version]
                      [?e :date date]]}
            version)
      ffirst))

(defn- get-version-at-time [version date]
  (-> (xt/q #_{:clj-kondo/ignore [:unresolved-symbol]}
       (xt/db xtdb-node date)
            '{:find  [(pull ?e [*])]
              :in    [version]
              :where [[?e :version version]]}
            version)
      ffirst))

(defn- get-versions-for-relations [relations]
  (->> relations
       (map #(get-version-at-time (first %) (second %)))
       (map update-date)))

(defn- get-relations-for-id [id]
  (xt/q #_{:clj-kondo/ignore [:unresolved-symbol]}
   (xt/db xtdb-node)
        '{:find  [version date]
          :where [[?e :xt/id version]
                  [?e :type :relation]
                  [?e :id id]
                  [?e :date date]]
          :in [id]
          :order-by [[date :asc]]}
        id))

(defn get-other-versions
  [id version]
  (->> id
       get-relations-for-id
       (remove #(= version (first %)))
       get-versions-for-relations))

(defn get-version [version]
  (when-let [date (get-date version)]
    (let [entity (-> (get-version-at-time version date)
                      update-date)
          id      (:xt/id entity)] 
      (assoc entity :versions (get-other-versions id version)))))

#_{:clj-kondo/ignore [:unresolved-var]}
(defn- update-entity [resource id version]
  (xt/submit-tx xtdb-node [[::xt/put (assoc resource 
                                            :xt/id id
                                            :version version
                                            :date (now)
                                            :type :entity)]])
  (xt/sync xtdb-node))

(defn- add-relation [id version date]
  (xt/submit-tx xtdb-node [[::xt/put {:xt/id version 
                                      :id id 
                                      :date date
                                      :type :relation}]])
  (xt/sync xtdb-node))

(defn upsert
  [resource id version]
  (if (exists? version)
    (throw (Exception. (format "Version already exists: %s" version)))
    (let [date (update-entity resource id version)]
      (add-relation id version date))))

(defn- get-all-relations []
  (xt/q #_{:clj-kondo/ignore [:unresolved-symbol]}
   (xt/db xtdb-node)
        '{:find  [version date]
          :where [[?e :xt/id version]
                  [?e :type :relation]
                  [?e :date date]]
          :order-by [[date :desc]]}))

(defn get-all []
  (-> (get-all-relations)
      (get-versions-for-relations)))
