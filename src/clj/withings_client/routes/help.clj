(ns withings-client.routes.help
  (:require
   [ring.util.response]
   [withings-client.layout :as layout]
   [withings-client.middleware :as middleware]))

(defn help-page [request]
  (layout/render request "help.html"))

(defn help-routes []
  ["/help"
   {:middleware [middleware/wrap-formats]}
   ["/" {:get help-page}]])
