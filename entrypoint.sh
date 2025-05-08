#!/bin/bash
set -e # Exit immediately if a command exits with a non-zero status

echo "--- API Compatibility Check Action Entrypoint ---"

# Environment variables are passed from the action.yml 'env' block:
# GH_PACKAGES_USER, GH_PACKAGES_PAT
# OLD_SPEC_FILE, NEW_SPEC_FILE, OUTPUT_FILE_NAME

# 1. Create Maven settings.xml dynamically
echo "Creating Maven settings.xml..."
SETTINGS_XML_PATH="/app/settings.xml" # Create in the WORKDIR
cat << EOF > ${SETTINGS_XML_PATH}
<settings>
  <activeProfiles>
    <activeProfile>github</activeProfile>
  </activeProfiles>
  <profiles>
    <profile>
      <id>github</id>
      <repositories>
        <repository>
          <id>github</id>
          <url>https://maven.pkg.github.com/apiprotector/API-Protector</url> <snapshots>
            <enabled>true</enabled>
          </snapshots>
        </repository>
      </repositories>
    </profile>
  </profiles>
  <servers>
    <server>
      <id>github</id>
      <username>${GH_PACKAGES_USER}</username>
      <password>${GH_PACKAGES_PAT}</password>
    </server>
  </servers>
</settings>
EOF
echo "settings.xml created at ${SETTINGS_XML_PATH}"

# 2. Download the JAR using Maven
echo "Downloading API Protector CLI JAR..."
JAR_ARTIFACT="com.apiprotector:api-protector-cli:2.0.1-SNAPSHOT" # Make this an input if it changes often
# Using --batch-mode for non-interactive execution, --quiet to reduce logs
# -Dmdep.stripVersion=false ensures the version is part of the filename, matching your grep logic
mvn dependency:copy -Dartifact=${JAR_ARTIFACT} -DoutputDirectory=. -Dmdep.stripVersion=false -DrepositoryId=github --settings ${SETTINGS_XML_PATH} --batch-mode --quiet
echo "JAR download command executed."

# 3. Locate and rename the JAR
echo "Locating and renaming JAR..."
# This grep looks for 'api-protector-cli-2.0.1-' followed by anything and ending in '.jar'
DOWNLOADED_JAR_NAME=$(ls | grep -E '^api-protector-cli-2\.0\.1-.*\.jar$' | head -n 1)

if [ -z "${DOWNLOADED_JAR_NAME}" ]; then
  echo "Error: Downloaded API Protector JAR not found in /app directory!"
  echo "Files in /app:"
  ls -al /app
  exit 1
fi
echo "Found JAR: ${DOWNLOADED_JAR_NAME}"
echo "ls - al"
ls -al
echo "pwd:"
pwd
mv "${DOWNLOADED_JAR_NAME}" "app.jar"

# For debugging, list files to confirm app.jar is present
ls -al /app

# 4. Execute the Java application
echo "Executing app.jar..."
JAVA_COMMAND_ARGS=()
JAVA_COMMAND_ARGS+=("${OLD_SPEC_FILE}")
JAVA_COMMAND_ARGS+=("${NEW_SPEC_FILE}")

# For debugging, list files to confirm app.jar is present
echo "checking where we are"
echo "checking where we are"

echo "checking where we are"

echo "checking where we are"

echo "checking where we are"

ls -al 


echo "checking what files are being provided"
echo "checking what files are being provided"

echo "checking what files are being provided"

echo "checking what files are being provided"
echo "${JAVA_COMMAND_ARGS[@]}"



echo "checking what is in /app directory"
echo "checking what is in /app directory"
echo "checking what is in /app directory"
echo "checking what is in /app directory"
ls -al /app


echo "${OLD_SPEC_FILE}"
echo "${NEW_SPEC_FILE}"

java -jar "app.jar" "${OLD_SPEC_FILE} ${NEW_SPEC_FILE}"

echo "--- API Compatibility Check Action Finished ---"