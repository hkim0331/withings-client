(ns withings-client.routes.services
 (:require
  [ring.util.http-response :as response]
  [withings-client.middleware :as middleware]
  ;; the reason why withings-client.users?
  ;; why not directly db.core?
  [withings-client.users :as users]))

(defn service-routes []
 ["/api"
  {:middleware [middleware/wrap-formats]}

  ["/users"  {:get (fn [_] (response/ok (users/users-list)))}]

  ["/user/:n"
   {:get
    (fn [{{:keys [n]} :path-params}]
      (response/ok (users/get-user n)))
    ;; UPDATE
    :post
    (fn [{{:keys [n]} :path-params :as request}]
      (let [params (:params request)]
        (response/ok {:user n :params params})))}]

  ["/user/del/:n"
   {:post
    (fn [{{:keys [n]} :path-params}]
      (try
        (users/delete-user! n)
        (response/ok "deleted")
        (catch Exception e
          (response/internal-server-error
           {:errors {:server-error (.getMessage e)}}))))}]

  ["/user"
   {:post
    (fn [{:keys [params]}]
      (try
        (users/create-user! params)
        (response/ok params)
        (catch Exception e
          ;; (println "error" (str params))
          (response/internal-server-error
           {:errors {:server-error (.getMessage e)}}))))}]])
