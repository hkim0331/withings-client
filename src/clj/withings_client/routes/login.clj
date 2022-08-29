(ns withings-client.routes.login
  (:require
   [clojure.tools.logging :as log]
   [ring.util.http-response :as response]
   [ring.util.response]
   [withings-client.layout :as layout]
   [withings-client.middleware :as middleware]
   [withings-client.tokens :as tokens]))

;; (defn callback
;;   "auth code inside request header :params {:code ... :state dev}"
;;   [{params :params}]
;;   (log/info "/callback" params)
;;   (tokens/fetch-and-store! params)
;;   (response/found "/"))

(defn login
  [request]
  (layout/render request
                 "login.html"
                 {:flash (:flash request)}))

;; FIXME: must be changed
(defn login!
  [{{:keys [login password]} :params}]
  (log/info "login" login "password" password)
  (if (and (seq password) (= login password))
    (-> (response/found "/home/")
        (assoc-in [:session :identity] login))
    (-> (response/found "/")
        (dissoc :session)
        (assoc :flash "login failure"))))

(defn logout!
  [_]
  (-> (response/found "/")
      (assoc-in [:session :identity] nil)))

(defn login-routes []
  [""
   {:middleware [;; middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/"  {:get  login
          :post login!}]
   ["/logout" {:get  logout!}]])
