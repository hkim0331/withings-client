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

