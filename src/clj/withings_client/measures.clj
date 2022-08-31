(ns withings-client.measures
  (:require
   [hato.client :as hc]
   ;; [java-time :as jt]
   ;; [clojure.string :as str]
   [clojure.tools.logging :as log]
   [withings-client.db.core :as db]
   [withings-client.misc :refer [datetime->timestamp]]
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


;; meastypes?
(defn meas
  "get meastype between startdate and enddate,
   also lastupdate param is available.
   using `access-token` value from `users` table.
   Returns the results in json format."
  [{:keys [id meastype startdate enddate lastupdate]}]
  (let [{:keys [access]} (users/get-user id)]
    (log/info "meas" id meastype startdate enddate lastupdate)
    (log/info "access" access)
    (-> (hc/post
         meas-uri
         {:as :json
          ;; "authorization" should be lower characters!
          :headers {"authorization" (str "Bearer " access)}
          :query-params
          {:action    "getmeas"
           :meastype  meastype
           :category  1
           :startdate  (datetime->timestamp startdate)
           :enddate    (datetime->timestamp enddate)
           :lastupdate (datetime->timestamp lastupdate)}})
        (get-in [:body :body]))))

(defn list-measures
  "returns measures vector"
  []
  (db/list-measures))
