#!/bin/bash

INFRA_DIR="infra/pulsar"

docker compose --project-name pulsar --file $INFRA_DIR/cluster.yaml up -d