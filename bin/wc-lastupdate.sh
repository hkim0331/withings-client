#!/bin/sh
# if [ -z "$2" ]; then
#     echo "usage:"
#     echo "$0 id lastupdate"
#     exit
# fi

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
https -pb --session=auth post wc.kohhoh.jp/api/meas id=${ID} meastype=1 lastupdate=${DATE}
