#!/bin/sh

SERV="ubuntu@kohhoh.jp"
DUMP="withings-`date +%F`.sql"

ssh ${SERV} 'cd wc/db-dumps && ./dump.sh'
scp ${SERV}:wc/db-dumps/${DUMP} .
# ./restore.sh ${DUMP}

