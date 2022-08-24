# Withings-Client

## Unreleased
- navbar のどこかに withings へのリンク
- kohhoh への早期配置。apt install mariadb-server
- create の隣に undo ボタン。
- mariadb のタイムスタンプを JST に。app.melt は JST だった。docker のが UTC.
- スリープすると mysql とのコネクションが切れる。
- edit-user-page
- on the fly で home-page 上の users をアップデートする。
- disable-user: valid をトグルする
- application を作ったときに cid, secret, が発行され、redirect-uri を指定する。開発 redirect-uri では hkimura ユーザしか試せない。
- edit-user


## 0.4.7 - 2022-08-24
- defined /user/:n/valid
- /user/delete/:n のルートがあるときに、/usr/:n/valid は定義できない。どっちにマッチするかわからない。/user/:n/delete に変更。
- new と users の上下を戻す
- wc.melt.kyutech.ac.jp で name='木村　広' のアクセストークン、リフレッシュトークンを取得できた。


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
