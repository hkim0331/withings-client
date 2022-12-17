#!/usr/bin/env bb
;; emulate
;; curl -c cookie.txt https://wc.kohhoh.jp/api/users

(ns users
  (:require [babashka.curl :as curl]
            [clojure.java.io :as io]
            [cheshire.core :as json]))

(def users-url "https://wc.kohhoh.jp/api/users")
(def cookie "cookie.txt")

(println (curl/get users-url {:raw-args ["-b" cookie]}))

