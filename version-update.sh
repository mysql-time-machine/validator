#!/bin/bash
source version

if [ "$#" -eq  "0" ]
   then
     ver=$DEFAULT_VERSION
 else
     ver=$1
fi
echo "USING VERSION:", $ver
echo "Don't forget to update the version in Dockerfile as well"

mvn versions:set -DnewVersion=$ver
