#!/bin/bash

docker exec -it pulsar-broker sh -c \
  "./bin/pulsar-admin functions create \
     --jar /etc/pulsar-functions/lib/dynamic-groovy-script-executor-0.0.1.nar \
     --function-config-file /etc/pulsar-functions/conf/dynamic-groovy-script-executor.yaml"