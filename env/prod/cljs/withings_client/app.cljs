(ns withings-client.app
  (:require [withings-client.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
