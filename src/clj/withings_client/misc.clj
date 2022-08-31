(ns withings-client.misc
  (:require
   [clojure.string :as str]
   [java-time :as jt]))

(defn datetime->timestamp
  "input:  yyyy-MM-DD hh:mm:ss
   return: timestamp (integer)"
  [s]
  (when (seq s)
    (let [[date time] (str/split s #" ")]
      (quot (-> (str date "T" time)
                jt/to-sql-timestamp
                jt/to-millis-from-epoch)
            1000))))

(comment
    (datetime->timestamp "2022-08-31 12:34:56")
    (<  (datetime->timestamp "2022-08-31 12:34:56")
        2000000000)
  )

(defn abbrev
  "default length = 8 abbreviation.
   if want other than 8 abbrev, say n,
   use (abbrev n s)"
  ([s]
   (abbrev 8 s))
  ([n s]
   (let [re (re-pattern (format "^(.{%d}).*" n))]
     (str/replace s re (str "$1" "...")))))

(comment
  (abbrev (-> (range 10) (str/join)))
  )