@echo off
java -XX:+UseG1GC -XX:MaxGCPauseMillis=50 -XX:+UseStringDeduplication -XX:CompileThreshold=100 -Xmx512m -Xms256m -jar runner.jar