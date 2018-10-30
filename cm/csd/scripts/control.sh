#!/bin/bash

set -x

# Time marker for both stderr and stdout
date; date 1>&2

# Preference order:
# 1. HBOARD_HOME (set by default_env.sh in the HBoard parcel).
# 2. CDH_HBOARD_HOME (set by cdh_env.sh in the CDH parcel).
# 3. Hardcoded default value (where the Cloudera packages install HBoard).
DEFAULT_HBOARD_HOME=/usr/lib/hboard
HBOARD_HOME=${HBOARD_HOME:-$CDH_HBOARD_HOME}
HBOARD_HOME=${HBOARD_HOME:-$DEFAULT_HBOARD_HOME}

HBOARD_CONF_DIR="$CONF_DIR/hboard-conf"

# Which java to use
if [ -z "$JAVA_HOME" ]; then
  JAVA="java"
else
  JAVA="$JAVA_HOME/bin/java"
fi

# Memory options
if [ -z "$JAVA_HEAP_OPTS" ]; then
  if [ -z "$SERVER_MAX_HEAP_SIZE" ]; then
    JAVA_HEAP_OPTS="-Xms1024m -Xmx2048m"
  else
    JAVA_HEAP_OPTS="-Xmx$SERVER_MAX_HEAP_SIZE"
  fi
fi

# JVM performance options
if [ -z "$JAVA_JVM_PERFORMANCE_OPTS" ]; then
  JAVA_JVM_PERFORMANCE_OPTS="-server -XX:+UseG1GC -XX:MaxGCPauseMillis=20 -XX:InitiatingHeapOccupancyPercent=35 -XX:+DisableExplicitGC -Djava.awt.headless=true"
fi

HBOARD_WEBAPP_NAME=/hboard
HBOARD_WEBAPP_MAIN=org.cboard.web.WebApp
HBOARD_WEBAPP_DIR=$HBOARD_HOME/webapp
HBOARD_LIB_DIR=$HBOARD_HOME/lib/*
HBOARD_RESOURCE_DIR=$HBOARD_HOME/resources
HBOARD_INIT_MAIN=org.cboard.InitMetadata

WEBAPP_ARGS="$HBOARD_WEBAPP_MAIN $SERVER_WEB_PORT $HBOARD_WEBAPP_NAME $HBOARD_WEBAPP_DIR"
JAVA_OPTS="$JAVA $JAVA_HEAP_OPTS $JAVA_JVM_PERFORMANCE_OPTS"
JAVA_CP="-cp $HBOARD_CONF_DIR:$HBOARD_LIB_DIR:$HBOARD_RESOURCE_DIR"

CMD=$1

function log {
  timestamp=$(date)
  echo "$timestamp: $1"       #stdout
  echo "$timestamp: $1" 1>&2; #stderr
}

if [ "start" = "$CMD" ]; then
  log "Starting HBoard Server"
  exec $JAVA_OPTS $JAVA_CP $WEBAPP_ARGS
elif [ "init_metadata" = "$CMD" ]; then
  log "Initing HBoard Metadata"
  exec $JAVA_OPTS $JAVA_CP $HBOARD_INIT_MAIN
else
  log "Don't understand [$CMD]"
fi
