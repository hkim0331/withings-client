#!/bin/sh

PATH=.:${PATH}
https --session=auth post wc.kohhoh.jp/api/token/${ID}/refresh >/dev/null
https -pb --session=auth wc.kohhoh.jp/api/users | wc-id-name.clj
