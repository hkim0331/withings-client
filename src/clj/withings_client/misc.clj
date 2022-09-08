(ns withings-client.misc
  (:require
   [clojure.tools.logging :as log]
   [clojure.string :as str]
   [java-time :as jt]))


(defn probe
  ([x]
   (probe x #(log/info "probe" %))) 
  ([x f]
   (f x)
   x))

(comment
  (-> 3
      probe
      (+ 3)
      probe))

(defn datetime->second
  "Convert date-time string into timestamp integer.
   Input is a string formatted as 'yyyy-MM-DD hh:mm:ss'.
   if lacked 'hh:mm:ss', supply '00:00:00' 
   Return value is timestamp, integer."
  [s]
  {:pre [(string? s)]}
  (let [[date time] (str/split s #" ")]
    (if (seq time)
      (-> (str date "T" time)
          jt/to-sql-timestamp
          jt/to-millis-from-epoch
          (quot 1000))
      (datetime->second (str date " 00:00:00")))))

;; (defn date->timestamp
;;   [s]
;;   {:pre [(string? s)]}
;;   (datetime->second (str s " 00:00:00")))

(defn timestamp->datetime
  "returns a string like '2022-08-31T12:34:56'
   should replace 'T' with ' '"
  [n]
  (-> (* n 1000)
      jt/instant->sql-timestamp
      jt/local-date-time
      str))

(defn abbrev
  "abbreviate string s. default 8 characters.
   if want other than 8, say n, use (abbrev n s)"
  ([s]
   (abbrev 8 s))
  ([n s]
   (let [re (re-pattern (format "^(.{%d}).*" n))]
     (str/replace s re (str "$1" "...")))))
