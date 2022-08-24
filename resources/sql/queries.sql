-- :name create-user! :! :n
-- :doc creates a new user record
INSERT INTO users
(name, cid, secret, belong, email)
VALUES
(:name, :cid, :secret, :belong, :email)

-- :name update-user! :! :n
-- :doc updates an existing user's records
UPDATE users
SET name = :name, belong = :belong, email = :email,
    cid = :cid, secret = :secret,
    access = :access, refresh = :refresh,
    updated_at = now()
WHERE id = :id

-- :name update-cid-by-name! :! :n
-- :doc updates an existing user's cid record
UPDATE users
SET cid = :cid, updated_at = now()
WHERE name = :name

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

-- :name user-by-name :? :1
-- :doc retrieves a user record given the name
SELECT * FROM users
where name = :name
