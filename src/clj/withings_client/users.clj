(ns withings-client.users
  (:require
   [clojure.tools.logging :as log]
   [withings-client.db.core :as db]))

(defn create-user!
  [params]
  (println "params" (str params))
  (db/create-user! params))

(defn get-user
  [id]
  (log/info "get-user" id)
  (db/get-user {:id id}))

(defn update-user!
  [params]
  (log/info "users" params)
  (db/update-user! params))

(defn delete-user!
  [id]
  (db/delete-user! {:id id}))

(defn users-list
  "returns users list reverse order of updated_at"
  []
  (log/info "users-list")
  (db/get-users))

(defn valid-users
  []
  (db/valid-users))

;; ;; no. update-user-by-name ではないと使い道がない。
;; (defn update-cid!
;;   [params]
;;   (db/update-cid-by-name! params))

(defn user-by-name
  [name]
  (db/user-by-name {:name name}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn update-tokens-by-name!
  "use from tokens/fetch-and-store!
   first insert user name, cid, secret,
   then updte the record with access, refresh token using key `name`"
  [params]
  (db/update-tokens-by-name! params))

(defn update-tokens!
  "Update id's access-token and refresh-token.
   Sometimes, refresh-token is not updated. So,
   return value is 1 or 2 when update successed."
  [params]
  (log/info "update-tokens!" params)
  (try
    (db/update-tokens! params)
    (catch Exception _ (throw (Exception. "update-token! can not update")))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn toggle-valid!
  "toggle colum valid"
  [id]
  (db/toggle-valid! {:id id}))