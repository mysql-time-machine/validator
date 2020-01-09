#!/bin/bash

java -Xdebug \
	-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 \
	-jar service/target/service-0.0.1.jar \
	--config-file validator-conf.yaml
