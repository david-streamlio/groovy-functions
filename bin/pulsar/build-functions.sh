#!/bin/bash

FUNCTIONS_DIR="groovy-functions"
INFRA_DIR="infra/pulsar/functions/lib"

# Check if the directory exists
if [ -d "$INFRA_DIR" ]; then
  # Check if the directory is empty
  if [ "$(ls -A $INFRA_DIR)" ]; then
    echo "Removing old artifacts...."
    rm -rf "$INFRA_DIR"/*.nar
    echo "Files deleted."
  fi
fi

echo "Building the function artifacts"
mvn clean install -f $FUNCTIONS_DIR/pom.xml

values=("groovy-script-executor" "dynamic-groovy-script-executor")

# Loop over each value
for val in "${values[@]}"; do
  cp $FUNCTIONS_DIR/${val}/target/*.nar $INFRA_DIR/
done
