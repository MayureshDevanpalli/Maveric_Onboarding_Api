#!/bin/bash
if [ -f app.pid ]; then
  PID=$(cat app.pid)
  echo "Stopping application with PID $PID..."
  kill $PID
  rm app.pid
  echo "Application stopped."
else
  echo "No PID file found. Application may not be running."
fi