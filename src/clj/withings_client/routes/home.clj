(ns withings-client.routes.home
  (:require
   [clojure.java.io :as io]
   [clojure.tools.logging :as log]
   [ring.util.http-response :as response]
   [ring.util.response]
   [withings-client.users :as users]
   [withings-client.layout :as layout]
   [withings-client.middleware :as middleware]))

(defn home-page [request]
  (layout/render request "home.html"))

;; auth code inside request header {:code ... :state dev}
(defn callback [{{:keys [cid state]} :params :as params}]
  (log/info "/callback" cid state)
  (users/update-cid! params)
  (response/ok "OK"))

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
  ;;  ["/docs" {:get (fn [_]
  ;;                   (-> (response/ok (-> "docs/docs.md" io/resource slurp))
  ;;                       (response/header "Content-Type" "text/plain; charset=utf-8")))}]
   ["/callback" {:get callback}]])
