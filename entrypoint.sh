#!/bin/bash
set -e

echo "--- API Compatibility Check Action Entrypoint ---"

# Build the command with required arguments
CMD="java -jar /app/app.jar \"${OLD_SPEC_FILE}\" \"${NEW_SPEC_FILE}\""

# Add optional output file if provided
if [ -n "${OUTPUT_FILE}" ]; then
    CMD="${CMD} --output \"${OUTPUT_FILE}\""
fi

# Add error flag if enabled
if [ "${EXIT_ON_ERROR}" = "true" ]; then
    CMD="${CMD} --error"
fi

# Add silent flag if enabled
if [ "${SILENT}" = "true" ]; then
    CMD="${CMD} --silent"
fi

# Add verbose flag if enabled
if [ "${VERBOSE}" = "true" ]; then
    CMD="${CMD} --verbose"
fi

echo "Executing: ${CMD}"
eval ${CMD}

echo "--- API Compatibility Check Action Finished ---"