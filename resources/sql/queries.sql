-- :name create-user! :! :n
-- :doc creates a new user record
INSERT INTO users
(name, cid, secret, belong, email)
VALUES
(:name, :cid, :secret, :belong, :email)

-- :name update-user! :! :n
-- :doc updates an existing user record
UPDATE users
-- FIXME:
SET first_name = :first_name, last_name = :last_name, email = :email
WHERE id = :id

-- :name get-user :? :1
-- :doc retrieves a user record given the id
SELECT * FROM users
WHERE id = :id

-- :name delete-user! :! :n
-- :doc deletes a user record given the id
DELETE FROM users
WHERE id = :id

-- :name get-users :? :*
-- :doc get all users
SELECT * FROM users
