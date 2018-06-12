#!/bin/bash

BIN_HOME=`dirname $0`
cd $BIN_HOME
BIN_HOME=`pwd`

cd $BIN_HOME/..
mvn clean package -Pweb
