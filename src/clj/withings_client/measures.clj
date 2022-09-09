# Withings-Client

## Unreleased
- (store fetched data in some DB)
- (push message via LINE)
- (undo facility)
- mainly getting many data at once
  change meastype with meastypes? should also change formatting functions.
- add `note` column to users table.
- bar chart of the viable time of access tokens
- validation about `startdate` and `enddate`
- async refresh-all
- favicon (nginx setting? '/' restriction?)
- css, color buttons
- display README.md or bin script usage.
- docker container AMD64 can rebuild for AARC64 if have Dockerfile?

## 0.11.0 - 2022-09-09
### Added
- docker-compose.yml
- initdb.d/
things left behind? near measures/probe?

## 0.10.5 - 2022-09-09
- display message(alert) if access-token need refresh.

## 0.10.4 - 2022-09-07
### Added
- bin/wc-id-name.clj
- bin/weights.sh
- bin/users.sh
### Fixed
- DateTimeFormatter -- "hh" is for 12-hours time format.
  "HH" is for 24-hour time format. How in CLJS? cljs-time?

## 0.10.3 - 2022-09-07
### Fixed
- forgot to merge? /meas returns (get in [:body :body])
  it should be (getin [:body :body :measuregrps])

## 0.10.2 - 2022-09-07
### Added
- bin/wc-date-value.clj

## 0.10.0 - 2022-09-03
- clojure -Tantq outdated :upgrade true

## 0.9.1 - 2022-09-02
- refactored continued. lastupdate, startdate, enddate.
- refactored output
- redisplay without reloading after updating/deleting

## 0.9.0 - 2022-09-02
- refactor. use session globally in `core.cljs`.

## 0.8.4 - 2022-09-01
- rewrote `core.cljs`s long components with functions. looks consice.

## 0.8.3 - 2022-09-01
- misc/datetime->second

## 0.8.2 - 2022-09-01
updating tokens remotely inside fetch script.
updated bin/ scripts.
- wc-login.sh _login_ _password_ -- to start a session.
  must be executed before before any of operations bellow.
- wc-users.sh -- get all users
- wc-toggle-valid.sh _id_ -- toggle users validity
- wc-lastupdate.sh _id_ _date_ -- fetch user id's last updated data.
- wc-start-end.sh _id_ _startdate_ _enddate_
  -- fetch user id's data between startdate and enddate.
- wc-refresh-all-auto.sh
- wc-refresh-all.sh -- refresh all existent tokens
- wc-refresh.sh _id_ -- refresh user id's token

## 0.8.1 - 2022-09-01
stopped auto refreshment of tokens at beginning of /mean.
keep independency of `refresh` and `fetch` functions.
instead, provide wc-lastupdate.sh and wc-start-end.sh,
in which execute refreshing tokens before actual fetching.

## 0.8.0 - 2022-09-01
- /dump-db/sync.sh -- syncronize localhost:withings with kohhoh:withings
- completion of datetime. supply time if omitted.

## 0.7.4 - 2022-08-31
- misc namespace. defined datetime->timestamp, abbrev,,,

## 0.7.3 - 2022-08-31
- lastupdate

## 0.7.2 - 2022-08-31
### Added
- defined /user/:id/valid: toggle the value of `valid`
- bin/ folder to keep wc scripts
- defined users/user-by-cid: return user record match with cid

### Changed
- update-tokens-by-userid! => update-tokens!
- shorten log

### Removed
- update-cid-by-name!

### Fixed
- two accounts can have same tokens. if updates one of them
  and the update success, will return 2 since userid is same.
  `tokens/update-tokens!` must be `update-tokens-by-userid!`
  see `0.7.1 Fixed`

## 0.7.1 - 2022-08-31
### Changed
- deploy.sh: divided `start.sh` into three, `{stop,start,restart}.sh`

### Added
- get "/api/valid-users"

### Fixed
- update-tokens-by-userid! returns 1 or 2 if success.
```
(defn restore!
  [params]
  (let [ret (users/update-tokens-by-userid! params)]
    (and (seq params) (pos? ret))))
```

## 0.7.0 - 2022-08-31
### Changed
- POST "/token/refresh-all" -> POST "/tokens/refresh-all"
- tokens/refresh-all -> tokens/refresh-all!
- tokens/refresh-and-restore-one! -> tokens/refresh-and-restore-id!
- stop using `reverse` in `core.cljs/output-component`


## 0.6.13 - 2022-08-31
- displays `98` as `98.00`
```
#(.toFixed % 2)
```
- defined /api/token/refresh-all

## 0.6.12 - 2022-08-30
- `login` and `password` environemt variables.
- change `valid` by 0 for false, 1 for true.

## 0.6.10 - 2022-08-30
- stop parinfer since it changes structure of codes. it leads some bugs.
- use r/atom for startdate and enddate

## 0.6.8 - 2022-08-30
- look after re-frame.
- stop using placeholder.
- remove `on-key-up` action.
- remove `(let [user (:user @session)])` from `core.cljs/user-page`

## 0.6.7 - 2022-08-30
- confirmed delete
- edit-user-page

## 0.6.6 - 2022-08-30
- polish up code.

### Changed
- /token/refresh/:n -> /token/:n/refresh

## 0.6.5 - 2022-08-29
- deploy to kohhoh
- timestamp->str function
```
 ;; (java.time.Instant/ofEpochMilli 1661330819000)
 ;; core.cljs
 (defn ts->date
  [ts]
  (.toLocaleString (js/Date. (* 1000 ts))))
```

## 0.6.4 - 2022-08-29
- CLJS timestamp functions (.toLocaleString (js/Date. ts))

## 0.6.3 - 2022-08-29
- GET /api/meas

### FIXME
- can not (migrate) SQL syntax error occurred.
  however, `mycli -u $USER $DB < $migration.sql` goes well.

## 0.6.2 - 2022-08-29
- depart callback from login.clj
- kohhoh did not update.
  -> used old deploy.sh for app.melt

### Added
- mysql/mysqldump can not dump maridb database.
- db-dump/{dump.sh,restore.sh}

## 0.6.1 - 2022-08-29
- departed /callback from login.clj
- session auth /api/
```
% http --session=auth :3000/ login='####' password='######'
% http --session=auth :3000/api/users ;; OK
% http :3000/api/users ;; NG
```
- demo weights between 2022-01-01 and 2022-09-30

## 0.6.0 - 2022-08-28
### Added
- buddy
```
  (ring/ring-handler
    (ring/router
      [(login-routes) (home-routes) (service-routes)]))
```
- without adding `wrap-auth` line in wrap-base function,
  could not access allowed pages.
```
(defn wrap-base [handler]
  (-> ((:middleware defaults) handler)
      wrap-auth ;; restriction
      wrap-flash))
```

## 0.5.2 - 2022-08-28
- core.cljs: added `:key` to seq elements
- core.cljs: delete user. need reload. improve.

## 0.5.1 - 2022-08-26
- demo

(def meas-uri "https://wbsapi.withings.net/measure")

;; curl
;; --header "Authorization: Bearer YOUR_ACCESS_TOKEN"
;; --data
;; action=getmeas&
;; meastype=meastype&  1 weight
;; meastypes=meastypes& list of meastype
;; category=category& 1 for realmeasure 2 for user objectives
;; startdate=startdate&
;; enddate=enddate&
;; offset=offset&
;; lastupdate=int use this  instead of startdate+enddate
;; 'https://wbsapi.withings.net/measure'

(defn check-response
 [{resp :body}]
 (when-not (= 200 (:status resp))
  (throw (Exception. "トークンが古いんじゃ？"))))

;; meastypes?
(defn meas
  "get meastype between `startdate` and `enddate`,
  `lastupdate` is also available.
   retrieve `access-token` from `users` table by `id`.
   it is required to fetch meas from withings.
   Returns the results in json format."
  [{:keys [id meastype startdate enddate lastupdate]}]
  (let [{:keys [access]} (users/get-user id)]
    (log/info "meas" id meastype startdate enddate lastupdate)
    (log/info "access token" (abbrev access))
    (-> (hc/post
         meas-uri
         {:as :json
          ;; CAUTION: "authorization" must be lower characters!
          :headers {"authorization" (str "Bearer " access)}
          :query-params
          {:action     "getmeas"
           :meastype   meastype
           :category   1
           :startdate  (datetime->second startdate)
           :enddate    (datetime->second enddate)
           :lastupdate (datetime->second lastupdate)}})
        ;; need validation
        ;; probe
        (get-in [:body :body :measuregrps]))))

(defn list-measures
  "returns measures items in vector"
  []
  (db/list-measures))
