#!/bin/sh

ROOTFS_VERSION_FILE=/etc/prplmesh/prplmesh_db_version
NVRAM_VERSION_FILE=/nvram/etc/prplmesh_db_version
PRPLMESH_DB=/nvram/etc/config/prplmesh
PRPLMESH_FACTORY_CMD=/opt/prplmesh/scripts/prplmesh_factory_reset.sh
UCI_UPGRADE_OPTION=prplmesh-user.db.upgrade
UPGRADE_USER=user
UPGRADE_FACTORY_INIT=factoryInit
UPGRADE_FACTORY_NEW=factoryNew
UPGRADE_MERGE=merge
RESET_CONF=reset_conf

PRPLMESH_CONF_ROOTFS_PATH=/opt/prplmesh/share
PRPLMESH_CONF_NVRAM_PATH=/nvram/
PRPLMESH_CONF_FILE_AGENT=beerocks_agent.conf 
PRPLMESH_CONF_FILE_CONTROLLER=beerocks_controller.conf

#####################################
# Helper functions for wlan ready
#####################################
brlan_ready()
{
        sysevent get multinet_1-status | grep ready &>/dev/null
}

hostapd_ready()
{
        hostapd_cli -i$1 stat &>/dev/null
}

wlan_reset()
{
        wifi down
        sleep 2
        wifi
}

wlan_ready()
{
        brlan_ready && hostapd_ready wlan0 && hostapd_ready wlan2
}

wlan_ready_poll()
{
        local delay=${1-3}
        while [ 0 == 0 ]; do
                if ! wlan_ready; then echo "$0: wlan not ready"; else break; fi
                sleep $delay
        done
}

prplmesh_enabled_poll()
{
        local oneshot=${1-false}
        local delay=${2-3}
        local retries=0

        while true; do
                prplmesh_enabled=$(uci get prplmesh.config.enable)
                [ $? == 0 ] && break
                [ "$oneshot" ] && break
                retries=$((retries+1))
                echo "$0: uci get prplmesh.config.enable failed, retrying #$retries..."
                sleep $delay
        done

        test "$prplmesh_enabled" == "1"
}

prplmesh_enabled()
{
        prplmesh_enabled_poll true
}

####################
# main
####################
status_function() {
        prplmesh_enabled && echo "prplMesh Enabled" || echo "prplMesh Disabled"
        wlan_ready && echo "WLAN Ready" || echo "WLAN not ready"
        /opt/prplmesh/scripts/prplmesh_utils.sh status
        echo "hostapd_wlan0 state:"
        hostapd_cli -iwlan0 stat | grep state=
        echo "hostapd_wlan2 state:"
        hostapd_cli -iwlan2 stat | grep state=
}

#if equal return 0, if $1>$2 return 1, if $1<=$2 return 2
compare_versions() {
        line_one=$(cat "$1")
        line_other=$(cat "$2")
        if [ "${line_one}" = "${line_other}" ]; then
                return 0;
        elif [ "${line_one}" '>' "${line_other}" ]; then
                return 1;
        else
                return 2;
        fi
}

update_db_version(){
	cp -f $ROOTFS_VERSION_FILE $NVRAM_VERSION_FILE
}

update_prplmesh_config() {
	if [[ ! -f "$PRPLMESH_CONF_NVRAM_PATH/$PRPLMESH_CONF_FILE_AGENT" ]] || [[ ! -f "$PRPLMESH_CONF_NVRAM_PATH/$PRPLMESH_CONF_FILE_CONTROLLER" ]]; then
	        $PRPLMESH_FACTORY_CMD $RESET_CONF
	fi
}

update_prplmesh_db() {

        #Run factoryReset cmd, if config is not generated
        if [ ! -e $PRPLMESH_DB -o ! -e $NVRAM_VERSION_FILE ]; then
                #DB doesn't exist - create it
                $PRPLMESH_FACTORY_CMD
                update_db_version
        else
                #if upgrade option is not set or isn't supported use the script parameter, otherwise use the uci.
                INIT_DB_OPTION="${1:-noparameter}"
                upgrade_option=$(uci get "$UCI_UPGRADE_OPTION")
                case $upgrade_option in
                        $UPGRADE_USER|$UPGRADE_FACTORY_INIT|$UPGRADE_FACTORY_NEW|$UPGRADE_MERGE)
                                INIT_DB_OPTION=$upgrade_option
                                ;;
                        *)
                                ;;
                esac

                case $INIT_DB_OPTION in
                        $UPGRADE_USER)
                                #user option - don't do anything
                                break
                                ;;
                        $UPGRADE_FACTORY_INIT)
                                #Factory reset on every init
                                $PRPLMESH_FACTORY_CMD
                                update_db_version
                                break
                                ;;
                        $UPGRADE_FACTORY_NEW)
                                #Factory reset if DB version is greater.
                                compare_versions $ROOTFS_VERSION_FILE $NVRAM_VERSION_FILE
                                if [ "$?" -eq 1 ]; then
                                        $PRPLMESH_FACTORY_CMD
                                        update_db_version					
                                fi
                                break
                                ;;
                        $UPGRADE_MERGE|*)
                                #Merge DB if version is greater.
                                compare_versions $ROOTFS_VERSION_FILE $NVRAM_VERSION_FILE
                                if [ "$?" -eq 1 ]; then
                                        $PRPLMESH_FACTORY_CMD $UPGRADE_MERGE
                                        update_db_version					
                                fi                             
                                ;;
                esac
        fi        
}

start_function() {        
        update_prplmesh_config
        update_prplmesh_db "${1:-noparameter}"
        if prplmesh_enabled; then
                mkdir -p /tmp/beerocks/pid/
                wlan_ready_poll
                echo "start prplmesh"
                /opt/prplmesh/scripts/prplmesh_utils.sh start
                /opt/prplmesh/scripts/beerocks_watchdog.sh &
        else
                echo "prplMesh Disabled (prplmesh.config.enable=0), skipping..."
                exit 0
        fi
}

stop_function() {
        echo "Stop prplMesh"
        /opt/prplmesh/scripts/prplmesh_utils.sh stop
        killall beerocks_watchdog.sh
}

enable_function() {
        echo "Enable prplMesh (reboot required)"
        uci set prplmesh.config.enable=1
        uci set wireless.radio0.acs_scan_mode=1
        uci set wireless.radio2.acs_scan_mode=1
        uci commit wireless
        uci commit prplmesh
}

disable_function() {
        echo "Disable prplMesh (reboot required)"
        uci set peplmesh.config.enable=0
        uci set wireless.radio0.acs_scan_mode=0
        uci set wireless.radio2.acs_scan_mode=0
        uci commit wireless
        uci commit prplmesh
}


case $1 in
        "start")
                start_function "${2:-noparameter}"
                ;;
        "stop")
                stop_function
                ;;
        "restart")
                stop_function
                wlan_reset
                start_function
                ;;
        "enable")
                enable_function
                ;;
        "disable")
                disable_function
                ;;
        "status")
                status_function
                ;;
        *)
        echo "Usage: $0 {start|stop|restart|enable|disable|status}"
esac
