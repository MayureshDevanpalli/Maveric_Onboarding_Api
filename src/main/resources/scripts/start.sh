#!/bin/bash

echo "Starting the application..."

# Find the JAR file (assumes only one JAR in the directory matching the pattern)
JAR_FILE=$(ls *.jar | head -n 1)

# Check if the JAR file exists
if [[ -z "$JAR_FILE" ]]; then
  echo "No JAR file found!"
  exit 1
fi

nohup java -jar "$JAR_FILE" > app.log 2>&1 &
echo $! > app.pid

echo "Application started in background. PID: $(cat app.pid), JAR: $JAR_FILE"