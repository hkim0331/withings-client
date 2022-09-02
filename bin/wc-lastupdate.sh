#!/bin/sh
if [ -z "$2" ]; then
    echo "usage:"
    echo "$0 id lastupdate"
    exit
fi

https --session=auth post wc.kohhoh.jp/api/token/$1/refresh
https --session=auth post wc.kohhoh.jp/api/meas id=$1 meastype=1 lastupdate="$2"
