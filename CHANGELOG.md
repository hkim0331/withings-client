# withings-client

## Unreleased
- CORS
localhost/:1 Access to XMLHttpRequest at 'https://account.withings.com/oauth2_user/authorize2?response_type=code&client_id=aaa&scope=user.metrics%2Cuser.activity&redirect_uri=https%3A%2F%2Fwc.melt.kyutech.ac.jp%2Fcallback&state=aaa' from origin 'http://localhost:3000' has been blocked by CORS policy: No 'Access-Control-Allow-Origin' header is present on the requested resource.

GET https://account.withings.com/oauth2_user/authorize2?response_type=code&client_id=aaa&scope=user.metrics%2Cuser.activity&redirect_uri=https%3A%2F%2Fwc.melt.kyutech.ac.jp%2Fcallback&state=aaa

- navbar のどこかに withings へのリンク
- secret はいつ使う？
- create のタイミングで name, belong, email を insert する。
- /callback で auth を受け取ったらすぐに access token, refresh token をゲットする。
- MySQL テーブルの定義。withings.users


## 0.2.0 - 2022-08-22
- URL を作ってクリックさせる方法で auth-token が取れた。
- gitignored /data フォルダ。


## 0.1.0 - 2022-08-22

## 0.0.0 - 2022-08-21
project started.
