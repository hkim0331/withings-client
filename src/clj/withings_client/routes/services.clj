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
            (try
              (tokens/refresh-and-restore-one! id)
              (response/ok "refreshed")
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
    (fn [{{:keys [n]} :path-params}]
      (response/ok (users/get-user n)))

    ;; /user/:n/update is right?
    :post
    (fn [{{:keys [n]} :path-params :as request}]
      (let [params (:params request)]
        (log/info "called `/user/:n`. sorry not yet implemented")
        (try
          (response/ok {:user n :params params})
          (catch Exception e (error e)))))}]

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
            (try
              (let [ret (measures/meas params)]
                (response/ok ret))
              (catch Exception e (error e))))
    :get (fn [_] (response/ok (measures/list-measures)))}]])
