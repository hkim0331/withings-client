(ns withings-client.routes.login
  (:require
   [clojure.tools.logging :as log]
   [ring.util.http-response :as response]
   [ring.util.response]
   [withings-client.config :refer [env]]
   [withings-client.layout :as layout]
   [withings-client.middleware :as middleware]
   #_[withings-client.tokens :as tokens]))

(defn login
  [request]
  (layout/render request
                 "login.html"
                 {:flash (:flash request)}))

(defn login!
  [{{:keys [login password]} :params}]
  (log/info "login" login "password" password)
  (log/info "env " (env :login) (env :password))
  (if (and (seq login)
           (seq password)
           (= login (env :login))
           (= password (env :password)))
    (-> (response/found "/home/")
        (assoc-in [:session :identity] login))
    (-> (response/found "/")
        (assoc :session {} :flash "login failure"))))

(defn logout!
  [_]
  (-> (response/found "/")
      (assoc-in [:session :identity] nil)))

(defn login-routes []
  [""
   {:middleware [middleware/wrap-formats]}
   ["/"        {:get login :post login!}]
   ["/logout"  {:get logout!}]])
