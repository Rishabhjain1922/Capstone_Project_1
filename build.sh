#!/usr/bin/env bash

# Set JAVA_HOME manually
export JAVA_HOME=/opt/render/project/.render/java

# Move to the actual backend folder
# shellcheck disable=SC2164
cd Evert-Organizer

# Make mvnw executable if it's not
chmod +x mvnw

# Run Maven build
./mvnw clean package
