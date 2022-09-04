#!/bin/sh
if [ -z "$1" ]; then
    echo usage: $0 yyyy-mm-dd.dumo
    exit 1
fi
DB=withings
CMD="mysql -u user -p -h 127.0.0.1 --port=3306"
${CMD} withings < $1

