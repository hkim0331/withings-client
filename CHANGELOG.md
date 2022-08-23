# Withings-Client

## Unreleased
- navbar のどこかに withings へのリンク
- secret はいつ使う？
- create のタイミングで name, belong, email を insert する。
- /callback で auth を受け取ったらすぐ access token, refresh token をゲット。
- kohhoh への早期配置。apt install mariadb-server
- create の隣に undo ボタン。
- mariadb のタイムスタンプを JST に。app.melt は JST だった。docker のが UTC.


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
