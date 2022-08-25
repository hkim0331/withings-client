(ns withings-client.tokens
  (:require
   [hato.client :as hc]
   ;; [clojure.string :as str]
   [clojure.tools.logging :as log]
   [withings-client.config :refer [env]]
   [withings-client.users :as users]))

(def oauth2-uri "https://wbsapi.withings.net/v2/oauth2")

(defn request-token
  "param is {:state state :code code},
   withings returns {:access token :refresh token :userid id}.
   Returns the map merging the withings thing and {:name state}"
  [{:keys [state code]}]
  (let [user (users/user-by-name state)]
    (-> (hc/post
         oauth2-uri
         {:as :json
          :query-params
          {:action        "requesttoken"
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

(defn refresh
  "when errors, returns {}"
  [{:keys [cid secret refresh] :as params}]
  (log/info "tokens/refresh params" params)
  (-> (hc/post
       oauth2-uri
       {:as :json
        :query-params
        {:action        "requesttoken"
         :grant_type    "refresh_token"
         :client_id     cid
         :client_secret secret
         :refresh_token refresh}})
      (get-in [:body :body])))

;; example
;; (refresh {:cid "f7164783bfc573217510d38c07176b798daa2a9e78edf1e320e6c1f0e5a5fa35"
;;           :secret "027a7105dfc049d9fe4722ab8f6dbdc66a3931702b57976f437af2368813e3de"
;;           :refresh "160395aa1d38b6635587ab4e30c410f5a6c86044"})

;; {:userid 13662696,
;;  :access_token "71781b62732d2e95ec4d2a349735cdb8b993b52d",
;;  :refresh_token "62cfc36a72820d50a6d83c3857849e978ff007b5",
;;  :scope "user.metrics,user.activity,user.info",
;;  :expires_in 10800,
;;  :token_type "Bearer"}

(defn restore!
  "params には userid, access, refresh, access, "
  [params]
  (log/info "tokens/restore! params" params)
  (if (empty? params)
    (throw (Exception. "empty param."))
    (users/update-tokens-by-userid! params)))

(defn refresh-and-restore!
  [{params :params}]
  (log/info "refresh-and-restore! params" params)
  (-> params refresh restore!))

(defn refresh-and-restore-one!
  [n]
  (-> (users/get-user n) refresh restore!))