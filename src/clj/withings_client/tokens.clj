(ns withings-client.tokens
  (:require
   [hato.client :as hc]
   [clojure.string :as str]
   [clojure.tools.logging :as log]
   [withings-client.config :refer [env]]
   [withings-client.users :as users]))

(defn auth
  "param is {:state name :code code}
   Returns {:access token :refresh token}"
  [{:keys [state code]}]
  (let [user (users/user-by-name state)
        ret (hc/post
             "https://wbsapi.withings.net/v2/oauth2"
             {:as :json
              :query-params {:action        "requesttoken"
                             :grant_type    "authorization_code"
                             :client_id     (user :cid)
                             :client_secret (user :secret)
                             :code          code
                             :redirect_uri  (env :redirect-url)}})
        body (get-in ret [:body :body])
        params (merge {:name state} body)]
    ;; (log/info "auth body" body)
    (log/info "auth params" params)
    (users/update-tokens-by-name! params)))
