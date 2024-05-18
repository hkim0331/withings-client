#!/bin/sh

SERV="ubuntu@kohhoh.jp"
DUMP="withings-`date +%F`.sql"

ssh ${SERV} 'cd withings-client/db-dumps && ./dump.sh'
scp ${SERV}:withings-client/db-dumps/${DUMP} .
# ./restore.sh ${DUMP}

