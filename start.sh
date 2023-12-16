#!/bin/sh
export PORT=4000
export DATABASE_URL="mysql://localhost:3306/withings?user=user&password=secret"
export REDIRECT_URL="https://wc.kohhoh.jp/callback"
export LOGIN="ice"
export PASSWORD="hockey"
export SLEEP=7200

java -jar withings-client.jar

