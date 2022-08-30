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
  (db/get-user {:id id}))


(defn update-user!
  [params]
  ;; (log/info "users/update-user! params" params)
  (db/update-user! params))

(defn delete-user!
  [id]
  (db/delete-user! {:id id}))

(defn users-list
  "returns users list reverse order of updated_at"
  []
  (db/get-users))

;; no. update-user-by-name ではないと使い道がない。
(defn update-cid!
  [params]
  (db/update-cid-by-name! params))

(defn user-by-name
  [name]
  (db/user-by-name {:name name}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn update-tokens-by-name!
  [params]
  (db/update-tokens-by-name! params))

(defn update-tokens-by-userid!
  [params]
  (log/info "update-tokens-by-userid!" params)
  (db/update-tokens-by-userid! params))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn toggle-valid!
  "toggle colum valid"
  [id]
  (db/toggle-valid! {:id id}))