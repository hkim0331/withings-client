(ns withings-client.routes.home
  (:require
   [ring.util.response]
   [withings-client.layout :as layout]
   [withings-client.middleware :as middleware]))

(defn home-page [request]
  (layout/render request "home.html"))

(defn home-routes []
  ["/home"
   {:middleware [middleware/wrap-restricted
                 middleware/wrap-formats]}
   ["/" {:get home-page}]])
