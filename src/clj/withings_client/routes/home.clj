(ns withings-client.routes.home
  (:require
   [clojure.tools.logging :as log]
   [ring.util.http-response :as response]
   [ring.util.response]
   [withings-client.layout :as layout]
   [withings-client.middleware :as middleware]
   [withings-client.tokens :as tokens]))

(defn home-page [request]
  (layout/render request "home.html"))

(defn home-routes []
  ["/home"
   {:middleware [middleware/wrap-restricted
                 middleware/wrap-formats]}
   ["/" {:get home-page}]])
