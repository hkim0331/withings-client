(ns withings-client.tokens
  (:require
   [hato.client :as hc]
   [clojure.string :as str]
   [clojure.tools.logging :as log]
   [withings-client.config :refer [env]]
   [withings-client.users :as users]))

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
         "https://wbsapi.withings.net/v2/oauth2"
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
