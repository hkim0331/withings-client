(ns withings-client.routes.home
  (:require
   [clojure.java.io :as io]
   [clojure.string :as str]
   [clojure.tools.logging :as log]
   [hato.client :as hc]
   [ring.util.http-response :as response]
   [ring.util.response]
   [withings-client.users :as users]
   [withings-client.layout :as layout]
   [withings-client.middleware :as middleware]))

(defn home-page [request]
  (layout/render request "home.html"))

;; curl --data "action=requesttoken
;;              &grant_type=authorization_code
;;              &client_id=7573fd4a4c421ddd102dac406dc6e0e0e22f683c4a5e81ff0a5caf8b65abed67
;;              &client_secret=d9286311451fc6ed71b372a075c58c5058be158f56a77865e43ab3783255424f
;               &code=mtwsikawoqleuroqcluggflrqilrnqbgqvqeuhhh
;;              &redirect_uri=https://www.withings.com"
;; 'https://wbsapi.withings.net/v2/oauth2'

(def base "https://wbsapi.withings.net/v2/oauth2")
(def m
  {"action" "requesttoken"
   "grant_type" "authorization_code"
   "client_id" "7573fd4a4c421ddd102dac406dc6e0e0e22f683c4a5e81ff0a5caf8b65abed67"
   "client_secret" "d9286311451fc6ed71b372a075c58c5058be158f56a77865e43ab3783255424f"
   "code" "mtwsikawoqleuroqcluggflrqilrnqbgqvqeuhhh"
   "redirect_uri" "https://www.withings.com"})

(defn create-url
  [base params]
  (str base "?"
       (->>  params
             (map #(str/join "=" %))
             (str/join "&"))))

(defn get-token
  [])

(defn refresh-token
  [])

;; auth code inside request header {:code ... :state dev}
(defn callback [{{:keys [code state]} :params :as params}]
  (log/info "/callback" code state)

  (response/found "/"))

(defn home-routes []
  [""
   {:middleware [middleware/wrap-csrf
                 middleware/wrap-formats]}
   ["/" {:get home-page}]
  ;;  ["/docs" {:get (fn [_]
  ;;                   (-> (response/ok (-> "docs/docs.md" io/resource slurp))
  ;;                       (response/header "Content-Type" "text/plain; charset=utf-8")))}]
   ["/callback" {:get callback}]])

