#!/bin/bash

mvn exec:java -Dexec.mainClass="interfuze.App" -Dexec.args="$1"
