(ns withings-client.users
  (:require
   [clojure.tools.logging :as log]
   [withings-client.db.core :as db]))

(defn create-user!
  [params]
  (log/info "create-user!")
  (db/create-user! params))

(defn get-user
  [id]
  (log/info "get-user" id)
  (let [ret (db/get-user {:id id})]
    (if (seq ret)
      ret
      (throw (Exception. "no such id")))))

(defn update-user!
  [user]
  (log/info "users" (:name user))
  (db/update-user! user))

(defn delete-user!
  [id]
  (db/delete-user! {:id id}))

(defn users-list
  "returns users list reverse order of updated_at"
  []
  (log/info "users-list")
  (db/get-users))

(defn valid-users
  "returns valid users list"
  []
  (log/info "valid-users")
  (db/valid-users))

;; ;; no. update-user-by-name ではないと使い道がない。
;; (defn update-cid!
;;   [params]
;;   (db/update-cid-by-name! params))

(defn user-by-name
  [name]
  (db/user-by-name {:name name}))

;; use?
;; (defn user-by-cid
;;   [cid]
;;   (db/user-by-cid {:cid cid}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn update-tokens-by-name!
  "updates the `name`s row with `access`, `refresh` token"
  [params]
  (log/info "users/update-tokens-by-name!" params)
  (try
    (db/update-tokens-by-name! params)
    (catch Exception _ (throw (Exception. "error: update-tokens-by-name!")))))

(defn update-tokens!
  "Update userid's access-token and refresh-token.
   returns the number of rows updated"
  [params]
  (log/info "users/update-tokens! params" params)
  (try
    (db/update-tokens! params)
    (catch Exception _ (throw (Exception. "error: update-tokens!")))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn toggle-valid!
  "toggle user id's `valid` column"
  [id]
  (db/toggle-valid! {:id id}))
