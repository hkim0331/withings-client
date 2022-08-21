(ns withings-client.env
  (:require
    [selmer.parser :as parser]
    [clojure.tools.logging :as log]
    [withings-client.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[withings-client started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[withings-client has shut down successfully]=-"))
   :middleware wrap-dev})
