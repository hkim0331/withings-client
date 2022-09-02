(ns withings-client.tokens
  (:require
   [clojure.tools.logging :as log]
   [hato.client :as hc]
   [withings-client.config :refer [env]]
   [withings-client.misc :refer [abbrev]]
   [withings-client.users :as users]))

;; 1. insert name, cid, secret
;; 2. request tokens with the `name`
;; 3. update records whose name column is `name`.

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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn refresh
  "withings's `requesttoken` requires `client id`.
   take the cid from the user data.
   when errors, returns {}"
  [{:keys [cid secret refresh]}]
  (log/info "tokens/refresh cid" (abbrev cid))
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
  "params = userid, access, refresh, access,
   returns true/false"
  [params]
  (let [ret (users/update-tokens! params)]
    (log/info "users/update-tokens! returns" ret)
    (and (seq params) (pos? ret))))

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
  (let [users (users/valid-users)]
    (log/info "tokens/refresh-all" (map :name users))
    (doseq [user users]
      (refresh-and-restore! user))))
