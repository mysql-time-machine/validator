#!/bin/bash
source version

java -Xdebug \
	-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 \
	-jar service-${DEFAULT_VERSION}.jar \
	--config-file config.yaml
