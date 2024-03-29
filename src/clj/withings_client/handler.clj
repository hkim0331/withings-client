(ns withings-client.handler
  (:require
   [withings-client.middleware :as middleware]
   [withings-client.layout :refer [error-page]]
   [withings-client.routes.help :refer [help-routes]]
   [withings-client.routes.home :refer [home-routes]]
   [withings-client.routes.login :refer [login-routes]]
   [withings-client.routes.callback :refer [callback-routes]]
   [withings-client.routes.services :refer [service-routes]]
   [reitit.ring :as ring]
   [ring.middleware.content-type :refer [wrap-content-type]]
   [ring.middleware.webjars :refer [wrap-webjars]]
   [withings-client.env :refer [defaults]]
   [mount.core :as mount]))

(mount/defstate init-app
  :start ((or (:init defaults) (fn [])))
  :stop  ((or (:stop defaults) (fn []))))

(mount/defstate app-routes
  :start
  (ring/ring-handler
    (ring/router
      [(callback-routes)
       (login-routes)
       (home-routes)
       (service-routes)
       (help-routes)])
    (ring/routes
      (ring/create-resource-handler
        {:path "/"})
      (wrap-content-type
        (wrap-webjars (constantly nil)))
      (ring/create-default-handler
        {:not-found
         (constantly (error-page {:status 404, :title "404 - Page not found"}))
         :method-not-allowed
         (constantly (error-page {:status 405, :title "405 - Not allowed"}))
         :not-acceptable
         (constantly (error-page {:status 406, :title "406 - Not acceptable"}))}))))

(defn app []
  (middleware/wrap-base #'app-routes))
