#!/bin/sh

PRPLMESH_DB_ROOTFS_PATH=/etc/prplmesh/
PRPLMESH_DB_NVRAM_PATH=/nvram/etc/config/
PRPLMESH_DB_ROOTFS_FILE=prplmesh_db
PRPLMESH_DB_NVRAM_FILE=prplmesh

UPGRADE_FACTORY_INIT=factoryInit
UPGRADE_FACTORY_NEW=factoryNew
UPGRADE_MERGE=merge
RESET_DB=reset_db
RESET_CONF=reset_conf

PRPLMESH_CONF_ROOTFS_PATH=/opt/prplmesh/config/
PRPLMESH_CONF_NVRAM_PATH=/nvram/
PRPLMESH_CONF_FILE_AGENT=beerocks_agent.conf 
PRPLMESH_CONF_FILE_CONTROLLER=beerocks_controller.conf

function merge(){
	full_reset
	echo "prplMesh factory reset merge done!" > /dev/console
}

function reset_db(){
	echo "Reseting prplMesh DB"  > /dev/console
	cp -f $PRPLMESH_DB_ROOTFS_PATH/$PRPLMESH_DB_ROOTFS_FILE $PRPLMESH_DB_NVRAM_PATH/$PRPLMESH_DB_NVRAM_FILE
}

function reset_conf(){
	echo "Reseting prplMesh configuration"  > /dev/console
	cp -f $PRPLMESH_CONF_ROOTFS_PATH/$PRPLMESH_CONF_FILE_AGENT $PRPLMESH_CONF_NVRAM_PATH/$PRPLMESH_CONF_FILE_AGENT
	cp -f $PRPLMESH_CONF_ROOTFS_PATH/$PRPLMESH_CONF_FILE_CONTROLLER $PRPLMESH_CONF_NVRAM_PATH/$PRPLMESH_CONF_FILE_CONTROLLER
}

function full_reset(){
	reset_db
	reset_conf
	echo "prplMesh factory reset done!" > /dev/console
}

case $1 in
	$UPGRADE_MERGE)
		merge
		;;
	$RESET_DB)
		reset_db
		;;
	$RESET_CONF)
		reset_conf
		;;				
	*)
		full_reset
		;;
esac