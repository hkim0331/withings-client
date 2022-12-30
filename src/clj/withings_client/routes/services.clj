(ns withings-client.routes.services
  (:require
   [clojure.tools.logging :as log]
   [ring.util.http-response :as response]
   [withings-client.measures :as measures]
   [withings-client.middleware :as middleware]
   [withings-client.tokens :as tokens]
   [withings-client.users :as users]))

(defn error
  [e]
  (response/bad-request (.getMessage e)))

(defn service-routes []
 ["/api"
  {:middleware [;; middleware/wrap-restricted
                middleware/wrap-formats]}

  ["/error"
   {:get (fn [_]
           (try
             (throw (Exception. "error occurs"))
             (catch Exception e (error e))))}]

  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; tokens. use also when creating user entry
  ["/token/:id/refresh"
   {:post (fn [{{:keys [id]} :path-params}]
            (log/info "/token/:id/refresh" id)
            (try
              (tokens/refresh-and-restore-id! id)
              (response/ok "success")
              (catch Exception e (error e))))}]

  ["/token/:id/toggle"
   {:post (fn [{{:keys [id]} :path-params}]
            (response/ok (users/toggle-valid! id)))}]

  ["/tokens/refresh-all"
   {:post (fn [_]
            (log/info "/tokens/refresh-all")
            (try
              (tokens/refresh-all!)
              (response/ok "refreshed")
              (catch Exception e (error e))))}]

  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; users
  ["/user"
   {:post
    (fn [{:keys [params]}]
      (try
        (users/create-user! params)
        (response/ok params)
        (catch Exception e (error e))))}]

  ["/users"
   {:get (fn [_] (response/ok (users/users-list)))}]

  ["/valid-users"
   {:get (fn [_] (response/ok (users/valid-users)))}]

  ["/user/:id"
   {:get
    (fn [{{:keys [id]} :path-params}]
      (try
        (response/ok (users/get-user id))
        (catch Exception e (error e))))
    :post
    (fn [{user :params}]
      (try
        (log/info "/user/:id, (:name user)" (:name user))
        (log/info "params" user)
        (users/update-user! user)
        (response/ok "updated")
        (catch Exception e (error e))))}]

  ["/user/:n/delete"
   {:post
    (fn [{{:keys [n]} :path-params}]
      (try
        (users/delete-user! n)
        (response/ok "deleted")
        (catch Exception e (error e))))}]

  ["/user/:id/valid"
   {:post
    (fn [{{:keys [id]} :path-params}]
      (try
        (users/toggle-valid! id)
        (response/ok "changed")
        (catch Exception e (error e))))}]

  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  ;; measures
  ["/measures"
   {:get (fn [_] (response/ok (measures/list-measures)))}]
  ["/meas"
   {:post (fn [{params :params}]
            (log/info "/meas " params)
            (try
              (response/ok (measures/meas params))
              (catch Exception e (error e))))}]
  #_["/meas-multi"
   {:post (fn [{params :params}]
            (log/info "/meas-multi " params)
            (try
              (response/ok (measures/meas-multi params))
              (catch Exception e (error e))))}]])
