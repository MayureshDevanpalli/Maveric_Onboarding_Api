#!/bin/bash
echo "Starting the application..."
nohup java -jar resume-standardizer-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
echo $! > app.pid
echo "Application started in background. PID: $(cat app.pid)"