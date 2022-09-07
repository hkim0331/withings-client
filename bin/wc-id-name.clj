#!/usr/bin/env bb

(doseq [{:keys [valid id name]}
        (json/parse-stream (clojure.java.io/reader *in*) true)]
  (when valid
    (println id name)))
