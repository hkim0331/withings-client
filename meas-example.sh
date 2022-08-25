#!/bin/sh
curl --header "Authorization: Bearer b8a3cfa6505c9a071eebee0480d5481295f7d339" --data "action=getmeas&meastype=1&category=1&startdate=startdate&enddate=enddate&offset=offset&lastupdate=int" 'https://wbsapi.withings.net/measure'
