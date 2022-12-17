#!/usr/bin/env bb
(require '[babashka.pods :as pods])
(pods/load-pod 'org.babashka/mysql "0.1.1")
(require '[pod.babashka.mysql :as sql])

(def db {:dbtype   "mysql"
         :host     "localhost"
         :port     3306
         :dbname   "withings"
         :user     (System/getenv "MYSQL_USER")
         :password (System/getenv "MYSQL_PASSWORD")})

;;(sql/execute! db ["select now()"])
;;(println (System/getenv "WC_LOGIN"))
