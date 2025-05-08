#!/bin/bash
set -e 

SETTINGS_XML_PATH="settings.xml"
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
JAR_ARTIFACT="com.apiprotector:api-protector-cli:2.0.1-SNAPSHOT" 

mvn dependency:copy -Dartifact=${JAR_ARTIFACT} \
-DoutputDirectory=. \
-Dmdep.stripVersion=false \
-DrepositoryId=github \
--settings ${SETTINGS_XML_PATH} \
--batch-mode \
--quiet
echo "JAR download command executed."

# 3. Locate and rename the JAR
echo "Locating and renaming JAR..."
DOWNLOADED_JAR_NAME=$(ls | grep -E '^api-protector-cli-2\.0\.1-.*\.jar$' | head -n 1)

if [ -z "${DOWNLOADED_JAR_NAME}" ]; then
  echo "Error: Downloaded API Protector JAR not found in /app directory!"
  exit 1
fi

echo "Found JAR: ${DOWNLOADED_JAR_NAME}"
mv "${DOWNLOADED_JAR_NAME}" "app.jar"
ls -al

#running java
echo "Running: java -jar /app/app.jar ${JAVA_COMMAND_ARGS[@]}"
java -jar "app.jar"

echo "--- API Compatibility Check Action Finished ---"