#!/bin/sh
if [ -z "$1" ]; then
    echo "usage:"
    echo "$0 id"
    exit
fi

https -pb --session=auth post wc.kohhoh.jp/api/token/$1/refresh
