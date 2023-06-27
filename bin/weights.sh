#!/bin/sh
# usage:
# $0
# $0 id
# $0 id lastupdate
# $0 id startdate enddate

if [ $# -eq 0 ]; then
  ID=102
  LASTUPDATE=`date +%F`
elif [ $# -eq 1 ]; then
  ID=$1
  LASTUPDATE=`date +%F`
elif [ $# -eq 2 ]; then
  ID=$1
  LASTUPDATE=$2
elif [ $# -eq 3 ]; then
  ID=$1
  STARTDATE=$2
  ENDDATE=$3
else
  echo usage:
  echo $0
  echo $0 id
  echo $0 id lastupdate
  echo $0 id startdate enddate
  exit
fi

PATH=.:${PATH}
https --session=auth post wc.kohhoh.jp/api/token/${ID}/refresh >/dev/null
https -pb --session=auth post wc.kohhoh.jp/api/meas \
  id=${ID} \
  meastype=1 \
  lastupdate=${LASTUPDATE} \
  startdate=${STARTDATE} \
  enddate=${ENDDATE} | wc-date-value.clj
