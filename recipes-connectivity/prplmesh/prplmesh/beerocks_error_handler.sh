#!/bin/sh

system_recovery() {
    # TODO: call recovery script
    :
}

#####################################
# MAIN
#####################################

bh_stopped=${1-0}
errors=${2-""}

echo "$0: Beerocks unrecoverable error $errors - Calling system recovery script"
system_recovery
echo "$0: completed"
