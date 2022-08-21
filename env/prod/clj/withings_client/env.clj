(ns withings-client.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[withings-client started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[withings-client has shut down successfully]=-"))
   :middleware identity})
