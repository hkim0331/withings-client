#!/bin/sh
if [ -z "$2" ]; then
    echo "usage:"
    echo "$0 login password"
    exit
fi

https -pb --session=auth wc.kohhoh.jp/ login=$1 password=$2
# https -pb --session=auth wc.kohhoh.jp/ login=`read` password=`read`
# curl -X POST -b cookie.txt -c cookie.txt -d "login=$1&password=$2" https://wc.kohhoh.jp/
