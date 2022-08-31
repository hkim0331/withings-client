#!/bin/sh
if [ -z "$1" ]; then
    echo "usage:"
    echo "$0 id <meastype> <start> <end>"
    exit
fi

M='1'
S='2022-01-01 00:00:00'
E='2023-01-01 00:00:00'

https --session=auth wc.kohhoh.jp/api/meas id=$1 meastype=${M} lastupdate="2022-08-31 00:00:00"

