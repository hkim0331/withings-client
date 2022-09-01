#!/bin/sh
if [ -z "$2" ]; then
    echo "usage:"
    echo "$0 login password"
    exit
fi

https --session=auth wc.kohhoh.jp/ login=$1 password=$2


