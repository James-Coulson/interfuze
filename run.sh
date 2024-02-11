#!/bin/bash

export JAVA_PROGRAM_ARGS=`echo "$@"`
mvn exec:java -Dexec.mainClass="interfuze.App" -Dexec.args="$JAVA_PROGRAM_ARGS"
