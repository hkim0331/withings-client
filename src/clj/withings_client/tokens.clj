(ns withings-client.tokens
  (:require
   [hato.client :as hc]
   [clojure.string :as str]
   [clojure.tools.logging :as log]
   [withings-client.config :refer [env]]
   [withings-client.users :as users]))

(def oauth2-uri "https://wbsapi.withings.net/v2/oauth2")

;; 0.4.8 resolved to `fetch` and `store!`
;; (defn auth
;;   "param is {:state name :code code}
;;    Returns {:access token :refresh token}"
;;   [{:keys [state code]}]
;;   (let [user (users/user-by-name state)
;;         ret (hc/post
;;              "https://wbsapi.withings.net/v2/oauth2"
;;              {:as :json
;;               :query-params {:action        "requesttoken"
;;                              :grant_type    "authorization_code"
;;                              :client_id     (user :cid)
;;                              :client_secret (user :secret)
;;                              :code          code
;;                              :redirect_uri  (env :redirect-url)}})
;;         body (get-in ret [:body :body])
;;         params (merge {:name state} body)]
;;     ;; (log/info "auth body" body)
;;     (log/info "auth params" params)
;;     (users/update-tokens-by-name! params)))

;; according to withings naming,
;; name this function as `request-token`.
(defn request-token
  "param is {:state state :code code},
   withings returns {:access token :refresh token :userid id}.
   Returns the map merging the withings thing and {:name state}"
  [{:keys [state code]}]
  (let [user (users/user-by-name state)]
    (-> (hc/post
         oauth2-uri
         {:as :json
          :query-params {:action        "requesttoken"
                         :grant_type    "authorization_code"
                         :client_id     (user :cid)
                         :client_secret (user :secret)
                         :code          code
                         :redirect_uri  (env :redirect-url)}})
        (get-in [:body :body])
        (merge {:name state}))))

(defn store!
  [params]
  (users/update-tokens-by-name! params))

(defn fetch-and-store!
  [params]
  (-> params request-token store!))

;; curl --data "action=requesttoken&
;; grant_type=refresh_token&
;; client_id=7573fd4a4c421ddd102dac406dc6e0e0e22f683c4a5e81ff0a5caf8b65abed67&
;; client_secret=d9286311451fc6ed71b372a075c58c5058be158f56a77865e43ab3783255424f&
;; refresh_token=9697c3d06ccfd1ca302f5a527d345a9f99ea88a2"
;; 'https://wbsapi.withings.net/v2/oauth2'
;;

(defn refresh-token
  [{:keys [cid secret refresh]}]
  (-> (hc/post
       oauth2-uri
       {:as :json
        :query-params {:action        "requesttoken"
                       :grant_type    "refresh_token"
                       :client_id     cid
                       :client_secret secret
                       :refresh_token refresh}})))

;; {
;;   "status": 0,
;;   "body": {
;;            "userid": "363",
;;            "access_token": "a075f8c14fb8df40b08ebc8508533dc332a6910a",
;;            "refresh_token": "f631236f02b991810feb774765b6ae8e6c6839ca",
;;            "expires_in": 10800,
;;            "scope": "user.info,user.metrics",
;;            "csrf_token": "PACnnxwHTaBQOzF7bQqwFUUotIuvtzSM",
;;            "token_type": "Bearer"}}

(defn update-tokens-by-userid
 [params]
 (users/update-tokens-by-userid! params))