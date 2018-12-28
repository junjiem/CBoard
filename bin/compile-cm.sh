#!/bin/bash

BIN_HOME=`dirname $0`
cd $BIN_HOME
BIN_HOME=`pwd`

yum install dos2unix -y
cd $BIN_HOME/..
dos2unix ./cm/csd/scripts/control.sh
mvn clean verify -Pcm
