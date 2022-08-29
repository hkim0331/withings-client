# Withings-Client

## Unreleased
- use English, particular in public repositries.
- undo facility.
- update users list on the fly. SPA way.
- remote debug
- redirection after refreshing tokens
- hato :async? true
- timestamp->str function
  (java.time.Instant/ofEpochMilli 1661330819000)
- visualize the viable time of access tokens.
* edit-user-page
  delete then create is OK now.
- omit reload after updating/deleting user information
- mainly getting many data at once.
- meastype -> meastypes
- parse-timestamp

## 0.6.4-SNAPSHOT
* pretty print JSON
* CLJS timestamp functions

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
