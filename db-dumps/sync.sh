#!/bin/sh
# wc.kohhoh.jp の mysql:withings とローカルのwithings データベースを
# 同期するプライベートスクリプト。

SERV="ubuntu@kohhoh.jp"
DUMP="withings-`date +%F`.sql"

ssh ${SERV} 'cd wc/db-dumps && ./dump.sh'
scp ${SERV}:wc/db-dumps/${DUMP} .
./restore.sh ${DUMP}

