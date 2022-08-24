(ns withings-client.routes.services
 (:require
  [ring.util.http-response :as response]
  [withings-client.middleware :as middleware]
  [withings-client.users :as users]))

(defn error
 [e]
 (response/internal-server-error
           {:errors {:server-error (.getMessage e)}}))

(defn service-routes []
 ["/api"
  {:middleware [middleware/wrap-formats]}

  ["/users"  {:get (fn [_] (response/ok (users/users-list)))}]

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
