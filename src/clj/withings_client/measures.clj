(ns withings-client.measures
  (:require
   [hato.client :as hc]
   [clojure.string :as str]
   [clojure.tools.logging :as log]
   [withings-client.config :refer [env]]
   [withings-client.users :as users]))

(def meas-uri "https://wbsapi.withings.net/measure")

;; curl
;; --header "Authorization: Bearer YOUR_ACCESS_TOKEN" 
;; --data 
;; action=getmeas&
;; meastype=meastype&  1 weight
;; meastypes=meastypes& list of meastype
;; category=category& 1 for realmeasure 2 for user objectives
;; startdate=startdate&
;; enddate=enddate&
;; offset=offset&
;; lastupdate=int use this  instead of startdate+enddate
;; 'https://wbsapi.withings.net/measure'

(defn meas
 [])

