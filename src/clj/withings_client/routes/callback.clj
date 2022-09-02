(ns withings-client.routes.callback
  (:require
   [clojure.tools.logging :as log]
   [ring.util.http-response :as response]
   [ring.util.response]
   [withings-client.middleware :as middleware]
   [withings-client.tokens :as tokens]))

(defn callback
  "auth code inside request header :params {:code ... :state dev}"
  [{params :params}]
  (log/info "/callback" params)
  (tokens/fetch-and-store! params)
  (response/found "/"))

(defn callback-routes []
  [""
   {:middleware [middleware/wrap-formats]}
   ["/callback" {:get callback}]])
