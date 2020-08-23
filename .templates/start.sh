#!/bin/bash
screen -S cloud java -XX:+UseG1GC -XX:+UseStringDeduplication -XX:MaxGCPauseMillis=50 -XX:CompileThreshold=100 -Xmx512m -Xms256m -jar runner.jar