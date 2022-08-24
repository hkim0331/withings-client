(ns withings-client.tokens
  (:require
   [hato.client :as hc]
   [clojure.string :as str]
   [clojure.tools.logging :as log]
   [withings-client.config :refer [env]]
   [withings-client.users :as users]))

;; curl --data "action=requesttoken
;;              &grant_type=authorization_code
;;              &client_id=7573fd4a4c421ddd102dac406dc6e0e0e22f683c4a5e81ff0a5caf8b65abed67
;;              &client_secret=d9286311451fc6ed71b372a075c58c5058be158f56a77865e43ab3783255424f
;;              &code=mtwsikawoqleuroqcluggflrqilrnqbgqvqeuhhh
;;              &redirect_uri=あれ。
;; 'https://wbsapi.withings.net/v2/oauth2'

;; auth only?
(def base "https://wbsapi.withings.net/v2/oauth2")

(defn create-url
  [params]
  (str base "?"
       (->>  params
             (map #(str/join "=" %))
             (str/join "&"))))

;; {"userid":"13662696",
;;  "access_token":"f89a8bc0037426c30ea2bada4495839f9befe55c",
;;  "refresh_token":"78ad5e78382f3368f7960321fb9a89b239ad29b6",
;;  "scope":"user.metrics,user.activity,user.info",
;;  "expires_in":10800,
;;  "token_type":"Bearer"}}

(defn auth
  "param is {:state name :code code}
   Returns {:access token :refresh token}"
  [{:keys [state code]}]
  (let [user (users/user-by-name state)
        ret (hc/post
             base
            {:query-params {:action        "requesttoken"
                            :grant_type    "authorization_code"
                            :client_id     (user :cid)
                            :client_secret (user :secret)
                            :code          code
                            :redirect_uri  (env :redirect-url)}})
        body (get-in ret [:body "body"])
        params (merge {:name state} body)]
    (log/info "auth body" body)
    (log/info "auth params" params)
    (users/update-tokens-by-name! params)))

(defn get-token
  [])

(defn refresh-token
  [])
