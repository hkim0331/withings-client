#!/bin/sh
mysqldump -u user -p -h 127.0.0.1 -P 3306 withings > withings-`date +%F`.sql
