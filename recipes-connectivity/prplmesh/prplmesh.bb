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

# TODO: correct DEPENDS for (non-)rdkb cases
# DEPENDS = "python-native python-pyyaml-native json-c openssl wpa-supplicant libnl"
DEPENDS = "python-native python-pyyaml-native json-c openssl nng wpa-supplicant libnl"
RDEPENDS_${PN} = "iproute2 busybox readline wpa-supplicant"

SRCREV = "d9eb170b69c1c225e40df5c191e8978568ec2e08"
SRC_URI = "git://github.com/prplfoundation/prplMesh.git;protocol=http;branch=master \
           file://001-add_find_nl_for_nl80211_bwl_type.patch"

BUILD_TYPE ?= "Release"
TARGET_PLATFORM ?= "linux"
MSGLIB ?= "zmq"
INSTALL_PREFIX ?= "/opt/prplmesh"

BEEROCKS_BRIDGE_IFACE ?= "br-lan"
BEEROCKS_WLAN1_IFACE ?= "wlan0"
BEEROCKS_WLAN2_IFACE ?= "wlan2"
BEEROCKS_ENABLE_ARP_MONITOR ?= "1"
BEEROCKS_LOG_FILES_ENABLED   ?= "true"
BEEROCKS_LOG_FILES_PATH      ?= "/tmp/beerocks/logs"
BEEROCKS_LOG_FILES_SUFFIX    ?= ".log"
BEEROCKS_LOG_FILES_AUTO_ROLL ?= "true"
BEEROCKS_LOG_STDOUT_ENABLED  ?= "false"
BEEROCKS_LOG_SYSLOG_ENABLED  ?= "false"

BWL_TYPE ?= "NL80211"
#
# MACHINE specific
#
BWL_TYPE_qemuarm ?= "DUMMY"
BWL_TYPE_qemuarm64 ?= "DUMMY"
BWL_TYPE_qemumips ?= "DUMMY"
BWL_TYPE_qemumips64 ?= "DUMMY"
BWL_TYPE_qemuppc ?= "DUMMY"
BWL_TYPE_qemux86 ?= "DUMMY"
BWL_TYPE_qemux86-64 ?= "DUMMY"
#
# "wlan0" is not activated on RPi
# So it's needed to plug Wi-Fi USB dongle to have two wlan iface
# Note: alternate variant is to use dummy iface
BEEROCKS_WLAN2_IFACE_raspberrypi-rdk-broadband = "wlan1"
BEEROCKS_BH_WIRE_IFACE_raspberrypi-rdk-broadband = "erouter0"
#
# RDKB specific
MSGLIB_rdk ?= "nng"
# temporary value for MSGLIB until check that prplMesh can start on RPi
# MSGLIB_rdk ?= "None"
BEEROCKS_BRIDGE_IFACE_rdk ?= "brlan0"
# BEEROCKS_BH_WIRE_IFACE_rdk ?= "nsgmii0"  # relevant for Puma7 only
BEEROCKS_ENABLE_ARP_MONITOR_rdk ?= "0"
BEEROCKS_LOG_FILES_ENABLED_rdk ?= "false"
BEEROCKS_LOG_SYSLOG_ENABLED_rdk  ?= "true"
BEEROCKS_LOG_FILES_AUTO_ROLL_rdk ?= "false"
BEEROCKS_LOG_FILES_PATH_rdk ?= "/rdklogs/logs"
BEEROCKS_LOG_FILES_SUFFIX_rdk ?= ".txt.0"

# DEPENDS_append = " @{bb.utils.contains(d.getVar('MSGLIB')}"

EXTRA_OECMAKE = "-DTARGET_PLATFORM=${TARGET_PLATFORM} \
                 -DBWL_TYPE=${BWL_TYPE} \
                 -DUSE_USER_TMP_PATH=0 \
                 -DCMAKE_INSTALL_PREFIX:PATH=${INSTALL_PREFIX} \
                 -DCMAKE_BUILD_TYPE=${BUILD_TYPE} \
                 -DCMAKE_VERBOSE_MAKEFILE:BOOL=ON \
                 -DBEEROCKS_BRIDGE_IFACE=${BEEROCKS_BRIDGE_IFACE} \
                 -DBEEROCKS_WLAN1_IFACE=${BEEROCKS_WLAN1_IFACE} \
                 -DBEEROCKS_WLAN2_IFACE=${BEEROCKS_WLAN2_IFACE} \
                 -DBEEROCKS_ENABLE_ARP_MONITOR=${BEEROCKS_ENABLE_ARP_MONITOR} \
                 -DBEEROCKS_LOG_FILES_ENABLED=${BEEROCKS_LOG_FILES_ENABLED} \
                 -DBEEROCKS_LOG_FILES_PATH=${BEEROCKS_LOG_FILES_PATH} \
                 -DBEEROCKS_LOG_FILES_SUFFIX=${BEEROCKS_LOG_FILES_SUFFIX} \
                 -DBEEROCKS_LOG_FILES_AUTO_ROLL=${BEEROCKS_LOG_FILES_AUTO_ROLL} \
                 -DBEEROCKS_LOG_STDOUT_ENABLED=${DBEEROCKS_LOG_STDOUT_ENABLED} \
                 -DBEEROCKS_LOG_SYSLOG_ENABLED=${BEEROCKS_LOG_SYSLOG_ENABLED} \
                 -DMSGLIB=${MSGLIB}"
# TODO: check is really need
#                 -DCMAKE_INCLUDE_PATH=${STAGING_INCDIR} 
#                 -DCMAKE_LIBRARY_PATH=${STAGING_LIBDIR} 
#                 -DCMAKE_SKIP_RPATH:BOOL=ON

do_install_append() {
    install -d ${D}${datadir}/cmake/Modules/
    mv ${D}${INSTALL_PREFIX}/lib/cmake/* ${D}${datadir}/cmake/Modules/
    rmdir ${D}${INSTALL_PREFIX}/lib/cmake

    rm -rf ${D}${INSTALL_PREFIX}/host
}

# prplMesh CMake files render conf files on configure stage
# If not say Yocto explicitly about such dependency
# then "do_configure" task will not rerun on variables values changing and
# there will be outdated prplMesh config files in board image
do_configure[vardeps] = "\
    BEEROCKS_BH_WIRE_IFACE \
    BEEROCKS_BRIDGE_IFACE \
    BEEROCKS_ENABLE_ARP_MONITOR \
    BEEROCKS_LOG_FILES_AUTO_ROLL \
    BEEROCKS_LOG_FILES_ENABLED \
    BEEROCKS_LOG_FILES_PATH \
    BEEROCKS_LOG_FILES_SUFFIX \
    BEEROCKS_LOG_STDOUT_ENABLED \
    BEEROCKS_LOG_SYSLOG_ENABLED \
    BEEROCKS_PLATFORM_INIT \
    BEEROCKS_REPEATER_MODE \
    BEEROCKS_WLAN1_IFACE \
    BEEROCKS_WLAN2_IFACE \
    "

# Explicit add paths to "*.so" files will be not needed when recipe start to
# build prplMesh almost from commit with "mapfcommon" library name fix
# See detais in PR https://github.com/prplfoundation/prplMesh/pull/1256

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
