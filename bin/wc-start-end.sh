#!/bin/sh
if [ -z "$3" ]; then
    echo "usage:"
    echo "$0 id startdate enddate"
    exit
fi

https --session=auth post wc.kohhoh.jp/api/token/$1/refresh
https --session=auth post wc.kohhoh.jp/api/meas id=$1 meastype=1 startdate="$2" enddate="$3"
