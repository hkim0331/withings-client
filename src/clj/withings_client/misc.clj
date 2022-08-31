(ns withings-client.misc
  (:require
   [clojure.string :as str]
   [java-time :as jt]))

(defn datetime->timestamp
  "input: yyyy-MM-DD hh:mm:ss
   returns timestamp(int)"
  [s]
  (when (seq s)
    (let [[date time] (str/split s #" ")]
      (quot (-> (str date "T" time)
                jt/to-sql-timestamp
                jt/to-millis-from-epoch)
            1000))))

(comment
  (defn f [s]
    (when (seq s)
      s))
  (f "abc")
  (f nil)
  )
