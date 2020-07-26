#!/bin/bash
echo "/home/root/prplmesh_startup.sh ran at $(date)!" > /tmp/prpl-debug
echo $(hostapd /usr/ccsp/wifi/hostapd0.conf) >> /tmp/prpl-debug
sleep 5
echo $(/opt/prplmesh/scripts/prplmesh_utils.sh start) >> /tmp/prpl-debug

