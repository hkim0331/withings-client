-- :name create-user! :! :n
-- :doc creates a new user record
INSERT INTO users
(name, cid, secret, belong, email)
VALUES
(:name, :cid, :secret, :belong, :email)

-- :name update-user! :! :n
-- :doc updates an existing user's records
UPDATE users
SET name     = :name,
    belong   = :belong,
    email    = :email,
    cid      = :cid,
    secret   = :secret,
    access   = :access, refresh = :refresh,
    valid    = :valid,
    line_id  = :line_id,
    bot_name = :bot_name,
    updated_at = now()
WHERE id = :id

-- -- :name update-cid-by-name! :! :n
-- -- :doc updates an existing user's cid record
-- UPDATE users
-- SET cid = :cid, updated_at = now()
-- WHERE name = :name

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
ORDER BY updated_at DESC

-- :name valid-users :? :*
-- :doc get all valid users
SELECT * FROM users
WHERE valid = TRUE

-- :name user-by-name :? :1
-- :doc retrieves a user record given the name
SELECT * FROM users
WHERE name = :name

-- :name user-by-cid :? :1
-- :doc retrieves a user record from cid
SELECT * FROM users
WHERE cid = :cid

-------------------------------------
-- tokens

-- :name update-tokens-by-name! :! :n
-- :doc updates an existing user's tokens, key is name.
UPDATE users
SET access = :access_token, refresh = :refresh_token, userid = :userid,
    updated_at = now()
WHERE name = :name

-- :name update-tokens! :! :n
-- :doc updates an existing user's tokens, key is name.
UPDATE users
SET access = :access_token, refresh = :refresh_token,
    updated_at = now()
WHERE userid = :userid

-------------------------------------
-- valid

-- :name toggle-valid! :! :n
-- :doc toggle id's `valid`
UPDATE users
SET valid = ! valid
WHERE id = :id

-------------------------------------
-- measures

-- :name list-measures :? :*
-- :doc get all measures
SELECT * FROM measures;
