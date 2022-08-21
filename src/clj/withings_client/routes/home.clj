(ns withings-client.routes.home
  (:require
   [withings-client.layout :as layout]
   [withings-client.db.core :as db]
   [clojure.java.io :as io]
   [withings-client.middleware :as middleware]
   [ring.util.response]
   [ring.util.http-response :as response]))

(defn home-page [request]
  (layout/render request "home.html"))

(defn callback [{params :params}]
  ;;(println (str params))
  ;;(response/ok (str params)))
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