#!/bin/sh
# retrieve today's measures of id=16

if [ -z "$1" ]; then
    ID=16
else
    ID=$1
fi
if [ -z "$2" ]; then
    DATE=`date +%F`
else
    DATE="$2"
fi

https --session=auth post wc.kohhoh.jp/api/token/${ID}/refresh >/dev/null
https -phb --session=auth post wc.kohhoh.jp/api/meas id=${ID} meastype=1 lastupdate=${DATE}
