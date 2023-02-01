#!/bin/sh

export BOOT_DIR=.
export BOOT_PORT=1984
export LOG_DIR=C:/temp/logs
export LOG_FILE=${LOG_DIR}/newApp.log

java -Xms512m -Xmx2048m -jar ${BOOT_DIR}/newApp-0.0.1-SNAPSHOT.jar


