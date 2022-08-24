(ns withings-client.routes.home
  (:require
   ;; [clojure.java.io :as io]
   ;; [clojure.string :as str]
   [clojure.tools.logging :as log]
   ;; [hato.client :as hc]
   [ring.util.http-response :as response]
   [ring.util.response]
   ;; [withings-client.users :as users]
   [withings-client.layout :as layout]
   [withings-client.middleware :as middleware]
   [withings-client.tokens :as tokens]))

(defn home-page [request]
  (layout/render request "home.html"))


(defn callback
  "auth code inside request header {:code ... :state dev}"
  [{{:keys [code state]} :params}]
  (log/info "/callback" "code" code "state" state)
  (tokens/auth {:name state :code code})
  (response/found "/"))

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/callback" {:get callback}]])

