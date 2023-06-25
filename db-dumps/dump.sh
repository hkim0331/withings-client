#!/bin/sh
mysqldump -u user -p -h localhost -P 13306 withings > withings-`date +%F`.sql
