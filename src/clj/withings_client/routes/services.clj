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
  (response/internal-server-error
   {:errors {:server-error (.getMessage e)}}))

(defn service-routes []
 ["/api"
  {:middleware [middleware/wrap-restricted
                middleware/wrap-formats]}

  ;; tokens. use also when creating user entry
  ["/token/:id/refresh"
   {:post (fn [{{:keys [id]} :path-params}]
            (log/info "/token/:n/refresh" id)
            (if (tokens/refresh-and-restore-id! id)
              (response/ok "success")
              (response/bad-request "fail")))}]

  ;; post?
  ["/tokens/refresh-all"
    {:post (fn [_]
             (log/info "/tokens/refresh-all")
             (try
               (tokens/refresh-all!)
               (response/ok "refreshed all")
               (catch Exception e (error e))))}]

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

  ["/user/:n"
   {:get
    (fn [{{:keys [id]} :path-params}]
      (response/ok (users/get-user id)))

    :post
    (fn [{params :params}]
      (try
        (log/info "/user/:id, params " params)
        (users/update-user! params)
        (response/ok "updated")
        (catch Exception e (error e))))}]

  ["/user/:n/delete"
   {:post
    (fn [{{:keys [n]} :path-params}]
      (try
        (users/delete-user! n)
        (response/ok "deleted")
        (catch Exception e (error e))))}]

  ["/user/:n/valid"
   {:post
    (fn [{{:keys [n]} :path-params}]
      (try
        (users/toggle-valid! n)
        (response/ok "valid")
        (catch Exception e (error e))))}]

  ;; measures
  ["/meas"
   {:post (fn [params]
            (log/info "/meas" params)
            (try
              (let [ret (measures/meas params)]
                (response/ok ret))
              (catch Exception e (error e))))
    :get (fn [_] (response/ok (measures/list-measures)))}]])
