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
   ["/users"
    {:get (fn [_] (response/ok (users/users-list)))}]
   ["/user"
    {:post
     (fn [{:keys [params]}]
       ;; (println "params" (str params))
       (try
         (users/create-user! params)
         (response/ok params)
         (catch Exception e
           (println "params" (str params))
           (response/internal-server-error
            {:errors {:server-error (.getMessage e)}}))))}]])