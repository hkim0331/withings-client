#!/bin/sh
lein uberjar
scp target/uberjar/withings-client.jar ubuntu@wc.kohhoh.jp:/srv/wc
ssh ubuntu@wc.kohhoh.jp 'cd /srv/wc && sh restart.sh'

