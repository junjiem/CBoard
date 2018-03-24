#!/bin/bash

set -x

# Time marker for both stderr and stdout
date; date 1>&2

TIMESTAMP=$(date)

CMD=$1

# Preference order:
# 1. CBOARD_HOME (set by cboard_env.sh in the CBoard parcel).
# 2. CDH_CBOARD_HOME (set by cdh_env.sh in the CDH parcel).
# 3. Hardcoded default value (where the Cloudera packages install CBoard).
DEFAULT_CBOARD_HOME=/usr/lib/cboard
export CBOARD_HOME=${CBOARD_HOME:-$CDH_CBOARD_HOME}
export CBOARD_HOME=${CBOARD_HOME:-$DEFAULT_CBOARD_HOME}

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
