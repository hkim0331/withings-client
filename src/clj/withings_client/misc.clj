(ns withings-client.misc
  (:require
   [clojure.string :as str]
   [java-time :as jt]))

(declare date->timestamp)

(defn datetime->timestamp
  "input:  yyyy-MM-DD hh:mm:ss
   return: timestamp (integer)"
  [s]
  (when (seq s)
    (let [[date time] (str/split s #" ")]
      (if (seq time)
        (quot (-> (str date "T" time)
                  jt/to-sql-timestamp
                  jt/to-millis-from-epoch)
              1000)
        (date->timestamp date)))))

(defn date->timestamp
  [s]
  (datetime->timestamp (str s " 00:00:00")))

(comment
  (throw (Exception. "example exception"))
  (datetime->timestamp "2022-08-31 12:34:56")
  (datetime->timestamp "2022-08-31")
  (<  (datetime->timestamp "2022-08-31 12:34:56")
      2000000000)
  (date->timestamp "2022-09-01"))

(defn abbrev
  "abbreviate string s. default 8 characters.
   if want other than 8, say n,  use (abbrev n s)"
  ([s]
   (abbrev 8 s))
  ([n s]
   (let [re (re-pattern (format "^(.{%d}).*" n))]
     (str/replace s re (str "$1" "...")))))

(comment
  (abbrev (-> (range 10) (str/join))))