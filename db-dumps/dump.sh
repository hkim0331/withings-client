#!/bin/sh
mysqldump -u user -p -h localhost -P 3306 withings > withings-`date +%F`.sql
