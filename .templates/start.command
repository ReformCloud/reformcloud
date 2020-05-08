#!/bin/bash
cd "$(dirname "$0")"
exec java -XX:+UseG1GC -XX:MaxGCPauseMillis=50 -XX:CompileThreshold=100 -Xmx512m -Xms256m -jar runner.jar