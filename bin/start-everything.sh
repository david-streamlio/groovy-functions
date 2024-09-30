#!/bin/bash

echo "Starting Apache Pulsar..."
sh ./bin/pulsar/start-pulsar.sh

sleep 15 && echo "Starting the groovy script source..."
sh ./bin/pulsar/start-groovy-script-feed.sh

sleep 15 && echo "Starting the groovy script executor..."
sh ./bin/pulsar/start-groovy-script-executor.sh
