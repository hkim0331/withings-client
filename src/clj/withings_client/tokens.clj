(ns withings-client.tokens
  (:require
   [clojure.tools.logging :as log]
   [hato.client :as hc]
   [withings-client.config :refer [env]]
   [withings-client.users :as users]))

(def oauth2-uri "https://wbsapi.withings.net/v2/oauth2")

(defn request-token
  "param is {:state state, :code code},
   withings returns {:access token, :refresh token, :userid id}.
   Returns the map merging {:name state} with the withings thing."
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

(defn refresh
  "when errors, returns {}"
  [{:keys [cid secret refresh]}]
  (log/info "tokens/refresh cid" cid)
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

(defn restore!
  "params には userid, access, refresh, access,
   returns true/false"
  [params]
  (log/info "tokens/restore! params" params)
  (and (seq params)
       (= 1 (users/update-tokens-by-userid! params))))

(defn refresh-and-restore!
  [user]
  (log/info "refresh-and-restore" (:name user))
  (-> user
      refresh
      restore!))

(defn refresh-and-restore-id!
  [id]
  (log/info "refresh-and-restore-id!" id)
  (-> (users/get-user id)
      refresh-and-restore!))

(defn refresh-all!
  []
  (let [users (users/users-list)]
    (log/info "tokens/refresh-all")
    (doseq [user users]
      (refresh-and-restore! user))))