(ns withings-client.routes.home
  (:require
   [clojure.java.io :as io]
   [clojure.tools.logging :as log]
   [ring.util.http-response :as response]
   [ring.util.response]
   [withings-client.db.core :as db]
   [withings-client.layout :as layout]
   [withings-client.middleware :as middleware]))

(defn home-page [request]
  (layout/render request "home.html"))

(defn callback [{params :params}]
  (log/info "/callback" params)
  (response/ok params))

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
   ["/docs" {:get (fn [_]
                    (-> (response/ok (-> "docs/docs.md" io/resource slurp))
                        (response/header "Content-Type" "text/plain; charset=utf-8")))}]
   ["/callback" {:get callback}]])