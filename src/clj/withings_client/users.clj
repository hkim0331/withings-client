(ns withings-client.users
  (:require
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
  (db/update-user! params))

(defn delete-user!
  [id]
  (db/delete-user! {:id id}))

(defn users-list
  []
  (db/get-users))
;; no. update-user-by-name ではないと使い道がない。
(defn update-cid!
  [params]
  (db/update-cid-by-name! params))

;; 2022-08-24 はここから
(defn user-by-name
  [name]
  (db/user-by-name {:name name}))

(defn update-tokens-by-name!
  [params]
  (db/update-tokens-by-name! params))