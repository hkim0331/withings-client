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

;; (defn refresh-token
;;   [{params :params}]
;;   (log/info "refresh-token" params)
;;   (response/ok {:refresh-token params}))

(defn service-routes []
 ["/api"
  {:middleware [middleware/wrap-formats]}

  ;; measures
  ["/meas"
   {:post #(try
             (let [ret (measures/meas %)]
                ;; (store ret)
               ret)
             (catch Exception e (error e)))}]
  
  ;; tokens
  ["/token/refresh"
   {:post #(do
             (tokens/refresh-and-restore! %)
             (response/ok "refreshed"))}]

  ["/token/refresh/:n"
   {:post (fn [{{:keys [n]} :path-params}]
            (log/info "/token/refresh/:n" n)
            (try
              (tokens/refresh-and-restore-one! n)
              (response/ok "refreshed")
              (catch Exception e (error e))))}]

  ;; users
  ["/users"
   {:get (fn [_] (response/ok (users/users-list)))}]

  ["/user/:n"
   {:get
    (fn [{{:keys [n]} :path-params}]
      (response/ok (users/get-user n)))

    ;; FIXME: UPDATE
    :post
    (fn [{{:keys [n]} :path-params :as request}]
      (let [params (:params request)]
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

  ["/user"
   {:post
    (fn [{:keys [params]}]
      (try
        (users/create-user! params)
        (response/ok params)
        (catch Exception e (error e))))}]])
