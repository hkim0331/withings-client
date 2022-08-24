(ns withings-client.tokens
  (:require
   [hato.client :as hc]
   [clojure.string :as str]
   [withings-client.users :as users]))

;; curl --data "action=requesttoken
;;              &grant_type=authorization_code
;;              &client_id=7573fd4a4c421ddd102dac406dc6e0e0e22f683c4a5e81ff0a5caf8b65abed67
;;              &client_secret=d9286311451fc6ed71b372a075c58c5058be158f56a77865e43ab3783255424f
;;              &code=mtwsikawoqleuroqcluggflrqilrnqbgqvqeuhhh
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

(defn auth
 "Returns {:access token :refresh token}
  param is {:name name :code code}"
  [{:keys [name code]}]
  (println "auth name" name "code" code))

(defn create-url
  [params]
  (str base "?"
       (->>  params
             (map #(str/join "=" %))
             (str/join "&"))))

(defn get-token
  [])

(defn refresh-token
  [])
