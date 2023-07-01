(ns withings-client.measures
  (:require
   [hato.client :as hc]
   [clojure.tools.logging :as log]
   [withings-client.db.core :as db]
   [withings-client.misc :refer [abbrev datetime->second probe]]
   [withings-client.users :as users]
   #_[withings-client.tokens :as tokens]))

(def meas-uri "https://wbsapi.withings.net/measure")

;; curl
;; --header "Authorization: Bearer YOUR_ACCESS_TOKEN"
;; --data
;; action=getmeas&
;; meastype=meastype&   1 weight
;; meastypes=meastypes& list of meastype
;; category=category&   1 for realmeasure 2 for user objectives
;; startdate=startdate&
;; enddate=enddate&
;; offset=offset&
;; lastupdate=int       use this  instead of startdate+enddate
;; 'https://wbsapi.withings.net/measure'
;; curl --header "Authorization: Bearer YOUR_ACCESS_TOKEN" --data "action=getmeas&meastype=meastype&meastypes=meastypes&category=category&startdate=startdate&enddate=enddate&offset=offset&lastupdate=int" 'https://wbsapi.withings.net/measure'
;; use meastypes?

(defn meas
  "get meastype between `startdate` and `enddate`,
  `lastupdate` is also available.
   retrieve `access-token` from `users` table by `id`.
   it is required to fetch meas from withings.
   Returns the results in json format."
  [{:keys [id meastype startdate enddate lastupdate] :as params}]
  (let [{:keys [access]} (users/get-user id)
        query-params
        (into {} (filter second
                         {:action     "getmeas"
                          :meastype   meastype
                          :category   1
                          :startdate  (datetime->second startdate)
                          :enddate    (datetime->second enddate)
                          :lastupdate (datetime->second lastupdate)}))]
    ;; (log/info "meas" id meastype startdate enddate lastupdate)
    ;; (log/info "params" params)
    (log/info "meas query-params:" query-params)
    (log/info "meas access token:" (abbrev access))
    (let [ret  (hc/post
                meas-uri
                {:as :json
                 ;; CAUTION: "authorization" must be lower characters!
                 ;; :headers {"authorization" (str "Bearer " access)}
                 :oauth-token  access
                 :query-params query-params})]
      (log/info "meas ret" ret)
      (if (= 200 (:status ret))
        (get-in ret [:body :body :measuregrps])
        (log/error "meas" ret)))))


(defn list-measures
  "returns measures items in vector"
  []
  (db/list-measures))
