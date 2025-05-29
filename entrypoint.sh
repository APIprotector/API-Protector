#!/bin/bash
set -e

echo "--- API Compatibility Check Action Entrypoint ---"
# 4. Execute the Java application
echo "Executing app.jar..."
# The OLD_SPEC_FILE and NEW_SPEC_FILE env vars are expected to be set by the action.yaml
java -jar "/app/app.jar" "${OLD_SPEC_FILE}" "${NEW_SPEC_FILE}"

echo "--- API Compatibility Check Action Finished ---"
