# Withings-Client

## Unreleased
- (store fetched data in some DB)
- (undo facility)
- mainly getting many data at once
  change meastype with meastypes? should also change formatting functions.
- add `note` column to users table.
- bar-chart of the viable time of access tokens
- display README.md or bin script usage.
- 新規ユーザの場合は userid をテーブルに入れず、リフレッシュは名前をキーとする。
- JST date update
- gap between `div.column`s.
- 403 は login ページへ。

## v1.30-SNAPSHOT
- gitignore start.sh
- git rm -r --cached .

## v1.29.606 / 2024-05-21
### Added
- Makefile:refresh
  upload/update withings-refresh.{service,timer}
### Changed
- cljs/core/refresh-all! pmap
- withings-refresh.service
```
  Type: oneshot
```
- withings-refresh.timer
  00:00:00 だと重なるか。
  RandomizedDelaySec=5m もやめておく。
```
  OnCalendar=*-*-* *:30:00
  # RandomizedDelaySec=5m
```

## v1.28.599 / 2024-05-19
- divided new to trhee pages. new, users, data.
- fix the problem after login failure.
### Removed
- Home item from navbar.
### Changed
- bump-version.sh updates `package.json`.


## v1.27.591 / 2024-05-19
- refresh-all を毎時呼ぶ。systemd? POST /api/tokens/refresh-all
### Added
- refresh-all.sh
- withings-refresh.service
- withings-refresh.timer
### Changed
- Makefile:timer-enable


## v1.26.586 / 2024-05-19
- CLJS では promise 使えない。
### Changed
- refresh-button takes `user-id` as its arg.
- added refresh-button to data component.
- fetch の結果を alert ではなく、テキストで表示。

## v1.25.579 / 2024-05-19
### Changed
- fetch data -> refresh and fetch
  最初の refresh が次の fetch に追い越されるのか、時間がかかる。
- CLJS の navbar, Withings-Client のリンクを "/" から "" にし、
  飛ばないように。

## v1.24.575 / 2024-05-18
### Changed
- db-dumps/refresh.sh: dir name wc->withings-client
- can refresh-tokens-all
### Added
- refresh-all button
  doseq で 30 秒くらい、pmap で1秒以内。ほんとか？

## v1.23.574 / 2024-05-18
- docker/mariadb-10 を利用する。コンテナでの開発よりも、表の macOS で開発。
- リモートログインして kohhoh.jp でサーバーデバッグしたら？
- ライブラリのバージョンアップは極力避けて、安定運用を目指す。
- updated bump-version.sh to update CHANGELOG.md
- hkim0331/luminus:0.3.2

<!-- restart development -->

## 0.22.0 - 2023-12-16
move to new kohhoh.jp.
also created `nuc.local:withings-client` drived by multipass. checked working.
### Added
- start.sh
- stop.sh
- restart.sh
- nginx/wc.kohhoh.jp(must strip lines added by certbot before starting)


## 0.21.1 - 2023-09-22
- bump-version.sh
- clojure -Tantq outdated :upgrade true

## 0.20.0 - 2023-06-29
- core.cljs/delete-button: delete user name ?
- create ボタン、リンクの説明を数行追加。

##  / 2024-05-18
- kohhoh# apt-mark hold mariadb-server
  mariadb 11.0.2 では以下のエラー。docker で mariadb 10.11 とならOK.
; Execution error (SQLException) at com.mysql.cj.jdbc.exceptions.SQLError/createSQLException (SQLError.java:130).
; Unknown system variable 'transaction_isolation'


##  / 2024-05-18
- fetch-and-restore! 時、userid が hkimura のものになってしまう。
- 表示テーブルに userid を入れる。

## 0.19.3 - 2023-06-26
- core.cljs: added table header
- core.cljs: added cid column in users-component

## 0.19.2 - 2023-06-25
- fixed:
```
;; 2023-06-25
(defn shorten [n s]
  (if (empty? s)
    [:span {:class "red"} "empty"]
    (str (subs s 0 n) "...")))
```
- fixed: did not show page /home
- updated libraries

```
|       :file |                             :name | :current |  :latest |
|-------------+-----------------------------------+----------+----------|
| project.clj |                  buddy/buddy-core | 1.10.413 | 1.11.418 |
|             |               buddy/buddy-hashers |  1.8.158 |  2.0.162 |
|             |                  buddy/buddy-sign |  3.4.333 |  3.5.346 |
|             | com.google.protobuf/protobuf-java |  3.21.12 |   3.23.3 |
|             |                   jonase/eastwood |    1.3.0 |    1.4.0 |
|             |                    metosin/reitit |   0.5.18 |    0.6.0 |
|             |       org.clojure/tools.namespace |    1.3.0 |    1.4.4 |
|             |       org.webjars/webjars-locator |     0.46 |     0.47 |
|             |                    ring/ring-core |    1.9.6 |   1.10.0 |
|             |                   ring/ring-devel |    1.9.6 |   1.10.0 |
```

## 0.19.0 - 2023-01-25
### Added
- Dockerfile
  devcontainer.json にかくサービスとしては withings-client,
  docker-compose.yml にかくサービスを実現するコンテナイメージが hkim0331/luminus.

## 0.18.0 - 2023-01-23
### Added
- /help/
### Updated
- base.html: 'Welcome to App' -> 'SAGA-JUDO'
- Makefile: all: clean deploy

## 0.17.0 - 2023-01-05

## 0.16.1 - 2022-12-31
- resumed restricted mode.

## 0.16.0 - 2022-12-31
- dividing namespaces.
- wc
- tokens
- show access codes on home-page.

## 0.15.5 - 2022-12-30
### Changed
- core.clj: remove nil values from `query-params`.

##  / 2024-05-18
### Changed
- /spi/meas uses api `meastypes`

## 0.14.2 - 2022-12-30
### Added
- Data: name, meatype をセレクトすると output フィールドをクリアする。
### Changed
- [:button "button"] から [:input.button {:value "button"}] に変更した。

## 0.14.1 - 2022-12-29
### Fixed
- フェッチする項目表示がずれてた。core.cljs/measure-name
### Changed
- update Makefile /target/default+uberjar -> /target/uberjar
- core.cljs (def redirect-uri "https://...") no warning.
```
-----------------------------------------------------------
 File: /Users/hkim/clojure/withings-client/src/cljs/withings_client/core.cljs:20:4
--------------------------------------------------------------------------------
  17 | ;; FIXME: better way?
  18 | (def redirect-uri
  19 |   (try
  20 |     js/redirectUrl
----------^-------------------------------------------------------
 unreachable code
```

## 0.13.2 - 2022-12-29
- libraries upgraded

## 0.13.1 - 2022-12-17
- can login with scripts/login.clj

## 0.13.0 - 2022-12-17
reactivate project
### migration
- (#'user/migrate) でできず、手動でマイグレートした。
### CHANGED
- bin/wc-date-value.clj: yyyy-mm-dd HH:mm:ss, ss 追加。
  体重も ww.w に直したい。93.60000000000001 のような表示が混ざる。
### kohhou にbabashka を準備
```
kohhoh$ sudo bash < <(curl -s https://raw.githubusercontent.com/babashka/babashka/master/install)
Downloading https://github.com/babashka/babashka/releases/download/v1.0.168/babashka-1.0.168-linux-amd64-static.tar.gz to /tmp/tmp.cWrCVk1z7i
Successfully installed bb in /usr/local/bin
```
### Added
- bin/update-meas.clj: login, fetch, store できること。


## 0.12.2 - 2022-11-14
- favicon.ico https://qiita.com/catatsuy/items/7589a3d7a10099f916ba
### Bugfix
- can not update line_id, bot_name field
  -> updated resources/sql/queries.sql

## 0.12.1 - 2022-11-11
- users テーブルに bot_name 追加。home.cljs もそれに伴い core.cljs に
  input フィールド追加。
- update users set bot_name='SAGA_JUDO' where id>20;

## 0.12.0 - 2022-11-11
- users テーブルに line_id 追加。
- input フィールド幅を Bulma の input クラスで。余白の多さを調整するより、入力値がすべて見えた方がいい。
- Makefile にエントリー uberjar, deploy, clean.
- kohhoh に withings-client.service

## 0.11.4 - 2022-09-30
- git add .clj-kondo

## 0.11.3 - 2022-09-09
- can remote debug using docker compose and vscode using

## 0.11.2 - 2022-09-09
VScode bug happens again.
A VScode buffer was overwritten by other buffer's content.
### Changed
- measures.clj replace check-respose with misc/probe
  probe function is safer. (no forget to return values)

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
      [(login-routes) (home-routes) (service-routes)])
```
- without adding `wrap-auth` line in wrap-base function,
  could not access allowed pages.
```
(defn wrap-base [handler]
  (-> ((:middleware defaults) handler)
      wrap-auth ;; restriction
      wrap-flash
```

## 0.5.2 - 2022-08-28
- core.cljs: added `:key` to seq elements
- core.cljs: delete user. need reload. improve.

## 0.5.1 - 2022-08-26
- demo

## 0.5.0 - 2022-08-26
- deploy as https://wc.kohhoh.jp

## 0.4.12 - 2022-08-26

### Added
- measure namespace
- timestamp int using java-time

### Info
- to append additional request header in hato, use :headers.
```
   (-> (hc/post
         meas-uri
         {:headers {"authorization" (str "Bearer " access)}
         ...
```

## 0.4.11 - 2022-08-26
- meas

### FIXME
- hato request header

## 0.4.10 - 2022-08-25
- /api/token/refresh/:id
- post /api/token/refresh:n

## 0.4.9 - 2022-08-25
- update-token. access-token expires 10800 sec.
- refresh
- /api/token/refresh
- tokens/refresh
- tokens/restore!
- tokens/refresh-and-restore!

## 0.4.8 - 2022-08-25
- resolve `tokens/auth` into `request-token` and `store!`,
  defined `tokens/fetch-and-store!`.
- list users reverse order.
  could not use (sort-by :update_at @users), maybe for tagged value.

## 0.4.7 - 2022-08-24
- link to Withings in navbar
- defined `/user/:n/valid`
- got tokens via `wc.melt.kyutech.ac.jp/callback`

### Changed
- routes conflict `/user/delete/:n` and `/usr/:n/valid`.
  changed to `/user/:n/delete` and `/user/:n/valid`. valid should be toggle-valid
- resume up and down `new` and `users` in `core.cljs`

## 0.4.6 - 2022-08-24
- users 表示。
- ページに version ナンバー
- #object[Transit$TaggedValue [TaggedValue: LocalDateTime, 2022-08-24T20:00:33.000]] を表示するため、tm を定義した。
```
(defn tm
  "returns strung yyyy-mm-dd hh:mm from tagged value rv"
  [^js/LocalDateTime tv]
  (let [s (.-rep tv)]
    (str (subs s 0 10) " " (subs s 11 16))))
```

## 0.4.5 - 2022-08-24
### Added
- secret は cid と共に、access/refresh を取得する際に必要。
- /callback で auth を受け取ったらすぐ access/refresh/userid をゲット。

### Changed
- users テーブルに userid varchar(255) 追加した。

## 0.4.4 - 2022-08-24
### Added
- 必須フィールドを(*)で表示
- users/user-by-name
- withings-client/tokens.clj
- core.cljs: (def redirect-uri js/redirectUrl)

dev_config.edn で次の定義をし、
```
:redirect-url "https://wc.melt.kyutech.ac.jp/callback"
```
利用したい ns で参照後、
```
[withings-client.config :refer [env]]
(env :redirect-url)
```
これを layout.clj/render の :params で渡すと JS にも渡る。
js/redirectUrl は、ブラウザを開く前、コンパイル時には未定義エラーになる。しょうがないね。


## 0.4.3 - 2022-08-23
- create のタイミングで name, cid のほか、
  埋めてあったら secret, belong, email を insert する。

### Fixed
- core.cljs: session アトムは上記すべてのフィールドをあらかじめ持ってる必要あり。

### Added
名前を揃えたほうがいいか？
- db/update-cid-by-name!
- users/update-user!
- db/updat-user!
- users/update-cid!

## 0.4.2 - 2022-08-23
- GET /api/users/:n
- POST /api/users/del/:n
- POST /api/user/:n for update(PUT)

## 0.4.1 - 2022-08-23
- created /src/routes/services.clj
- created /src/users.clj
- GET /api/users
- POST /api/user {:name "name" :cid "cid"}
- create のタイミングで name, belong, email を insert する。
  対応する値が "" の時、null が "" に書き換わる。

## 0.3.0 - 2022-08-23
- Makefile -- npm install xmlhttprequest
- secret フィールド追加
- mariadb, withings データベース、テーブル users を定義した。
- wc.melt で auth token 取得できることを確認。
- start.sh: app.melt で java -jar したスクリプトのコピー。gitignored しておく。

## 0.2.0 - 2022-08-22
- ajax-cljs の GET は CORS に抵触する

  localhost/:1 Access to XMLHttpRequest at 'https://account.withings.com/oauth2_user/authorize2?response_type=code&client_id=aaa&scope=user.metrics%2Cuser.activity&redirect_uri=https%3A%2F%2Fwc.melt.kyutech.ac.jp%2Fcallback&state=aaa' from origin 'http://localhost:3000' has been blocked by CORS policy: No 'Access-Control-Allow-Origin' header is present on the requested resource.

  GET https://account.withings.com/oauth2_user/authorize2?response_type=code&client_id=aaa&scope=user.metrics%2Cuser.activity&redirect_uri=https%3A%2F%2Fwc.melt.kyutech.ac.jp%2Fcallback&state=aaa
- URL を作ってクリックさせる方法で auth-token が取れた。
- gitignored /data フォルダを作った。

## 0.1.0 - 2022-08-22
- https://wc.melt 準備。callback は HTTPS じゃないとダメになった。
- そのほか、変更になったものは？
- withings の用語の揺れに注意。cid が client_id, あとなんだ？

## 0.0.0 - 2022-08-21
project started.
