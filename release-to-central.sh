#!/bin/bash
cd data/ && mvn deploy -P release -DskipTests=true
