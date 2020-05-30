# SPDX-License-Identifier: BSD-2-Clause-Patent
# SPDX-FileCopyrightText: 2020 the prplMesh contributors (see AUTHORS.md)
# This code is subject to the terms of the BSD+Patent license.
# See LICENSE file for more details.

inherit cmake pythonnative

S = "${WORKDIR}/git"
OECMAKE_SOURCEPATH = "${S}/"

SUMMARY = "prplMesh"
SECTION = "net"
LICENSE = "BSD-2-Clause-Patent.txt"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0518d409dae93098cca8dfa932f3ab1b"

DEPENDS = "python-native python-pyyaml-native json-c openssl"
RDEPENDS_${PN} = "iproute2 busybox readline"

SRCREV = "8a331d9fa1631782cf06603ee612ce6133ee0c1a"
SRC_URI = "git://github.com/prplfoundation/prplMesh.git;protocol=http;branch=master \
           file://001-add_find_nl_for_nl80211_bwl_type.patch \
           file://002-allow_redefine_beerocks_vars_from_cmdline.patch \
           file://003_prplmesh_util_stop_use_hard_coded_wlan_iface_names.patch \
           file://004-get_hostapd_ctrl_iface_pathes_from_conf.patch"


PACKAGECONFIG ?= "${BWL_TYPE} ${MSGLIB} ${BUILD_TYPE}"

BUILD_TYPE ?= "release"
TARGET_PLATFORM ?= "linux"
MSGLIB ?= "mq-zmq"
BWL_TYPE ?= "bwl-nl80211"
INSTALL_PREFIX ?= "/opt/prplmesh"
#
# QEMU MACHINE specific
#
BWL_TYPE_qemuarm ?= "bwl-dummy"
BWL_TYPE_qemuarm64 ?= "bwl-dummy"
BWL_TYPE_qemumips ?= "bwl-dummy"
BWL_TYPE_qemumips64 ?= "bwl-dummy"
BWL_TYPE_qemuppc ?= "bwl-dummy"
BWL_TYPE_qemux86 ?= "bwl-dummy"
BWL_TYPE_qemux86-64 ?= "bwl-dummy"
#
# RPi machine specific
#
# "wlan2" is not activate on RPi. So it's needed to plug-in Wi-Fi
# USB dongle to have two wlan ifaces: wlan0 and wlan1
BEEROCKS_WLAN1_IFACE_raspberrypi-rdk-broadband = "wlan0"
BEEROCKS_WLAN2_IFACE_raspberrypi-rdk-broadband = "wlan1"
BEEROCKS_HOSTAP_WLAN1_CTRL_IFACE_raspberrypi-rdk-broadband="/var/run/hostapd0/wlan0"
BEEROCKS_HOSTAP_WLAN2_CTRL_IFACE_raspberrypi-rdk-broadband="/var/run/hostapd4/wlan1"
BEEROCKS_BH_WIRE_IFACE_raspberrypi-rdk-broadband = "erouter0"

PACKAGECONFIG_append_raspberrypi-rdk-broadband = " \
    wlan1_iface \
    wlan2_iface \
    hostap_wlan1_ctrl_iface \
    hostap_wlan2_ctrl_iface \
    bh_wire_iface"
#
# RDKB specific
# "ZeroMG" has incompatible license for RDKB so use NNG
MSGLIB_rdk ?= "mq-nng"

BEEROCKS_BRIDGE_IFACE_rdk ?= "brlan0"
#
# FIXME: There is a bug in script printing status
#        if log files paths are not standard
# So temporary disable redefinition to use defaults
# BEEROCKS_LOG_FILES_ENABLED_rdk ?= "false"
# BEEROCKS_LOG_SYSLOG_ENABLED_rdk ?= "true"
# BEEROCKS_LOG_FILES_AUTO_ROLL_rdk ?= "false"
# BEEROCKS_LOG_FILES_PATH_rdk ?= "/rdklogs/logs"
# BEEROCKS_LOG_FILES_SUFFIX_rdk ?= ".txt.0"
#
PACKAGECONFIG_append_rdk = " \
    bridge_iface \
    "
# PACKAGECONFIG_append_rdk = " \
#     bridge_iface \
#     log_files_enabled \
#     log_syslog_enabled \
#     log_files_auto_roll \
#     log_files_path \
#     log_files_suffix \
#     "

EXTRA_OECMAKE = "-DTARGET_PLATFORM=${TARGET_PLATFORM} \
                 -DUSE_USER_TMP_PATH=0 \
                 -DCMAKE_INSTALL_PREFIX:PATH=${INSTALL_PREFIX} \
                 -DCMAKE_VERBOSE_MAKEFILE:BOOL=ON \
                 "

# TODO: check is really need
#                 -DCMAKE_INCLUDE_PATH=${STAGING_INCDIR}
#                 -DCMAKE_LIBRARY_PATH=${STAGING_LIBDIR}
#                 -DCMAKE_SKIP_RPATH:BOOL=ON

PACKAGECONFIG[release] = "-DCMAKE_BUILD_TYPE=Release,,,"
PACKAGECONFIG[debug] = "-DCMAKE_BUILD_TYPE=Debug,,,"

PACKAGECONFIG[mq-zmq] = "-DMSGLIB=zmq,,zeromq,"
PACKAGECONFIG[mq-nng] = "-DMSGLIB=nng,,nng,"
PACKAGECONFIG[mq-none] = "-DMSGLIB=None,,nng,"

PACKAGECONFIG[bwl-nl80211] = "-DBWL_TYPE=NL80211,,wpa-supplicant libnl,wpa-supplicant"
PACKAGECONFIG[bwl-dummy] = "-DBWL_TYPE=DUMMY,,wpa-supplicant libnl,wpa-supplicant"
PACKAGECONFIG[bwl-dwpal] = "-DBWL_TYPE=DWPAL,,libnl ubus uci wav-dpal,"

PACKAGECONFIG[bridge_iface] = "-DBEEROCKS_BRIDGE_IFACE=${BEEROCKS_BRIDGE_IFACE},,,"
PACKAGECONFIG[bh_wire_iface] = "-DBEEROCKS_BH_WIRE_IFACE=${BEEROCKS_BH_WIRE_IFACE},,,"
PACKAGECONFIG[wlan1_iface] = "-DBEEROCKS_WLAN1_IFACE=${BEEROCKS_WLAN1_IFACE},,,"
PACKAGECONFIG[wlan2_iface] = "-DBEEROCKS_WLAN2_IFACE=${BEEROCKS_WLAN2_IFACE},,,"
PACKAGECONFIG[wlan3_iface] = "-DBEEROCKS_WLAN3_IFACE=${BEEROCKS_WLAN3_IFACE},,,"
PACKAGECONFIG[log_files_enabled] = "-DBEEROCKS_LOG_FILES_ENABLED=${BEEROCKS_LOG_FILES_ENABLED},,,"
PACKAGECONFIG[log_files_path] = "-DBEEROCKS_LOG_FILES_PATH=${BEEROCKS_LOG_FILES_PATH},,,"
PACKAGECONFIG[log_files_suffix] = "-DBEEROCKS_LOG_FILES_SUFFIX=${BEEROCKS_LOG_FILES_SUFFIX},,,"
PACKAGECONFIG[log_files_auto_roll] = "-DBEEROCKS_LOG_FILES_AUTO_ROLL=${BEEROCKS_LOG_FILES_AUTO_ROLL},,,"
PACKAGECONFIG[log_stdout_enabled] = "-DBEEROCKS_LOG_STDOUT_ENABLED=${BEEROCKS_LOG_STDOUT_ENABLED},,,"
PACKAGECONFIG[log_syslog_enabled] = "-DBEEROCKS_LOG_SYSLOG_ENABLED=${BEEROCKS_LOG_SYSLOG_ENABLED},,,"
PACKAGECONFIG[hostap_wlan1_ctrl_iface] = "-DBEEROCKS_HOSTAP_WLAN1_CTRL_IFACE=${BEEROCKS_HOSTAP_WLAN1_CTRL_IFACE},,,"
PACKAGECONFIG[hostap_wlan2_ctrl_iface] = "-DBEEROCKS_HOSTAP_WLAN2_CTRL_IFACE=${BEEROCKS_HOSTAP_WLAN2_CTRL_IFACE},,,"
PACKAGECONFIG[hostap_wlan3_ctrl_iface] = "-DBEEROCKS_HOSTAP_WLAN3_CTRL_IFACE=${BEEROCKS_HOSTAP_WLAN3_CTRL_IFACE},,,"
PACKAGECONFIG[wpa_supplicant_wlan1_ctrl_iface] = "-DBEEROCKS_WPA_SUPPLICANT_WLAN1_CTRL_IFACE=${BEEROCKS_WPA_SUPPLICANT_WLAN1_CTRL_IFACE},,,"
PACKAGECONFIG[wpa_supplicant_wlan2_ctrl_iface] = "-DBEEROCKS_WPA_SUPPLICANT_WLAN2_CTRL_IFACE=${BEEROCKS_WPA_SUPPLICANT_WLAN2_CTRL_IFACE},,,"
PACKAGECONFIG[wpa_supplicant_wlan3_ctrl_iface] = "-DBEEROCKS_WPA_SUPPLICANT_WLAN3_CTRL_IFACE=${BEEROCKS_WPA_SUPPLICANT_WLAN3_CTRL_IFACE},,,"

do_install_append() {
    install -d ${D}${datadir}/cmake/Modules/
    mv ${D}${INSTALL_PREFIX}/lib/cmake/* ${D}${datadir}/cmake/Modules/
    rmdir ${D}${INSTALL_PREFIX}/lib/cmake

    rm -rf ${D}${INSTALL_PREFIX}/host
}

FILES_${PN} += "\
    ${INSTALL_PREFIX}/bin \
    ${INSTALL_PREFIX}/lib/lib*.so.* \
    ${INSTALL_PREFIX}/share \
    ${INSTALL_PREFIX}/scripts \
    ${INSTALL_PREFIX}/config \
"

FILES_${PN}-dev += "\
    ${INSTALL_PREFIX}/include \
    ${INSTALL_PREFIX}/lib/*.so \
    ${datadir}/cmake/Modules \
"
