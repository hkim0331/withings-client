#!/bin/sh
#
# origin: ${utils}/tools/src/bump-version.sh
#
# last update: 2023-09-22
#
# CAUSION:
# The POSIX standard regular expressions does not support back-references.
# Back-references are considered as an "extended" faciliy.
# This script, bump-version.sh, uses the extended function.
# So, gnu-sed on macOS.

if [ -z "$1" ]; then
    echo "usage: $0 <version>"
    exit 1
fi

if [ -x "${HOMEBREW_PREFIX}/bin/gsed" ]; then
    SED="${HOMEBREW_PREFIX}/bin/gsed -E"
else
    SED="/usr/bin/sed -E"
fi

${SED} -i "s|(defproject \S+).*|\1 \"$1\"|" project.clj

#${SED} -i "s|(def \^:private version) .+|\1 \"$1\")|" src/core.clj

${SED} -i "s|(def \^:private version).*|\1 \"$1\")|" src/cljs/withings_client/core.cljs

# CHANGELOG.md
TODAY=`date +%F`
${SED} -i -e "/SNAPSHOT/c\
## ${VER} / ${TODAY}" CHANGELOG.md
