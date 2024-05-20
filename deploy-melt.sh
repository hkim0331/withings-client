#!/bin/sh
lein uberjar
scp target/uberjar/withings-client.jar app.melt:wc/
ssh app.melt 'cd wc && sh start.sh'
