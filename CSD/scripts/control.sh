#!/bin/bash

set -x

# Time marker for both stderr and stdout
date; date 1>&2

TIMESTAMP=$(date)

CMD=$1

echo "CMD: $CMD"
echo "SERVER_WEB_PORT: $SERVER_WEB_PORT"


if [ "$CMD" = "start" ]; then
    echo "$TIMESTAMP Starting Server on port $SERVER_WEB_PORT"

elif [ "$CMD" = "stop" ]; then
    echo "$TIMESTAMP Stopping Server"

elif [ "$CMD" = "create_metadata_tables" ]; then
    echo "$TIMESTAMP Create Metadata Tables"

else
    echo "$TIMESTAMP Don't understand [$CMD]"
    exit 2
fi
