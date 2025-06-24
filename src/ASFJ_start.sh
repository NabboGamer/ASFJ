#!/usr/bin/env bash
#
# Start ASFJ fat JAR with necessary VM-args

# Script Directory
# This allows the script to be run from any directory
# and still find the JAR in the same directory as the script.
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# JAR path
JAR="$SCRIPT_DIR/ASFJ-1.0.0.jar"

# Check if the JAR file exists 
if [[ ! -f "$JAR" ]]; then
  echo "Error: file $JAR not found."
  exit 1
fi

# VM's options
VM_OPTS=(
  "-DLOG_LEVEL=INFO"
  "-Dfirewallgui.application.guice.stage=production"
)

# Start the application
exec java "${VM_OPTS[@]}" -jar "$JAR"
