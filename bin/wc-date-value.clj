#!/usr/bin/env bb
(import
 '[java.time LocalDateTime Instant ZoneId]
 '[java.time.format DateTimeFormatter])

;; DEBUG
;; (def jd (json/parse-string (slurp "data/data.json") true))
;; (def jd (json/parse-stream (clojure.java.io/reader *in*) true))

(defn- sq
  [n]
  (* n n))

(defn- pow
  "returns b's power of n. n must be positive."
  [b n]
  (cond
    (zero? n) 1N
    (even? n) (sq (pow b (/ n 2)))
    :else (* b (pow b (dec n)))))

(defn power
  ""
  [b n]
  (cond
    (pos? n) (pow b n)
    (zero? n) 1
    :else (/ 1 (pow b (- n)))))

(defn instant->datetime
  [instant]
  (LocalDateTime/ofInstant instant (ZoneId/of "Asia/Tokyo")))

(defn milli->datetime
  "input milli,
   returns LocalDateTime object"
  [milli]
  (-> milli
      Instant/ofEpochMilli
      (instant->datetime)))

(defn second->datetime
  [second]
  (milli->datetime (* 1000 second)))

(defn second->str
  ([n]
   (second->str "yyyy-MM-dd hh:mm:dd" n))
  ([fmt n]
   ;; (println "n" n)
   (->> (second->datetime n)
        (.format (DateTimeFormatter/ofPattern fmt)))))

(defn print-date-value
  [{:keys [date measures]}]
  (let [{:keys [value unit]} (first measures)]
    ;; (println date value unit)
    (println (second->str date) (* 1.0 value (power 10 unit)))))

;;;;;;;;;;;;;;;;;;;;;
;; main starts here
(doseq [j (json/parse-stream (clojure.java.io/reader *in*) true)]
  ;; (println (str j))
  (print-date-value j))
