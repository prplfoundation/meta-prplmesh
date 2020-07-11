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

DEPENDS = "python-native python-pyyaml-native json-c openssl readline"
RDEPENDS_${PN} = "iproute2 busybox"

SRCREV = "d55b727b52158d46b3a0263734445ac29116220f"
SRC_URI = "git://github.com/prplfoundation/prplMesh.git;protocol=http;branch=master \
	  "

PACKAGECONFIG ??= "${BWL_TYPE} ${MSGLIB}"

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

PACKAGECONFIG[debug] = "-DCMAKE_BUILD_TYPE=Debug,-DCMAKE_BUILD_TYPE=Release,,"

PACKAGECONFIG[mq-zmq] = "-DMSGLIB=zmq,,zeromq,"
PACKAGECONFIG[mq-nng] = "-DMSGLIB=nng,,nng,"
PACKAGECONFIG[mq-none] = "-DMSGLIB=None,,nng,"

# Add explicit linking beerocks bwl lib with wpa_client lib
# When OpenWrt toolchain build prplMesh the linker use object file
# "wpa_ctrl.c.o" directly from hostapd internal build folder
# But it's not possible in Yocto environment so add dependency on
# "wpa-supplicant" recipe to use "wpa_client" library which it builds
PACKAGECONFIG[bwl-nl80211] = "-DBWL_TYPE=NL80211 -DBWL_LIBS=wpa_client,,wpa-supplicant libnl,wpa-supplicant"
PACKAGECONFIG[bwl-dummy] = "-DBWL_TYPE=DUMMY,,wpa-supplicant libnl,wpa-supplicant"
PACKAGECONFIG[bwl-dwpal] = "-DBWL_TYPE=DWPAL,,libnl ubus uci wav-dpal,"

EXTRA_OECMAKE = "-DTARGET_PLATFORM=${TARGET_PLATFORM} \
                 -DUSE_USER_TMP_PATH=0 \
                 -DCMAKE_INSTALL_PREFIX:PATH=${INSTALL_PREFIX} \
                 -DCMAKE_VERBOSE_MAKEFILE:BOOL=ON \
                 "

def oecmake_var (name, d):
    v = d.getVar(name, True)
    return '-D{}={}'.format(name, v) if v else ''

EXTRA_OECMAKE += "${@oecmake_var('BEEROCKS_BRIDGE_IFACE', d)}"
EXTRA_OECMAKE += "${@oecmake_var('BEEROCKS_BH_WIRE_IFACE', d)}"
EXTRA_OECMAKE += "${@oecmake_var('BEEROCKS_WLAN1_IFACE', d)}"
EXTRA_OECMAKE += "${@oecmake_var('BEEROCKS_WLAN2_IFACE', d)}"
EXTRA_OECMAKE += "${@oecmake_var('BEEROCKS_WLAN3_IFACE', d)}"
EXTRA_OECMAKE += "${@oecmake_var('BEEROCKS_LOG_FILES_ENABLED', d)}"
EXTRA_OECMAKE += "${@oecmake_var('BEEROCKS_LOG_FILES_PATH', d)}"
EXTRA_OECMAKE += "${@oecmake_var('BEEROCKS_LOG_FILES_SUFFIX', d)}"
EXTRA_OECMAKE += "${@oecmake_var('BEEROCKS_LOG_FILES_AUTO_ROLL', d)}"
EXTRA_OECMAKE += "${@oecmake_var('BEEROCKS_LOG_STDOUT_ENABLED', d)}"
EXTRA_OECMAKE += "${@oecmake_var('BEEROCKS_LOG_SYSLOG_ENABLED', d)}"
EXTRA_OECMAKE += "${@oecmake_var('BEEROCKS_HOSTAP_WLAN1_CTRL_IFACE', d)}"
EXTRA_OECMAKE += "${@oecmake_var('BEEROCKS_HOSTAP_WLAN2_CTRL_IFACE', d)}"
EXTRA_OECMAKE += "${@oecmake_var('BEEROCKS_HOSTAP_WLAN3_CTRL_IFACE', d)}"
EXTRA_OECMAKE += "${@oecmake_var('BEEROCKS_WPA_SUPPLICANT_WLAN1_CTRL_IFACE', d)}"
EXTRA_OECMAKE += "${@oecmake_var('BEEROCKS_WPA_SUPPLICANT_WLAN2_CTRL_IFACE', d)}"
EXTRA_OECMAKE += "${@oecmake_var('BEEROCKS_WPA_SUPPLICANT_WLAN3_CTRL_IFACE', d)}"

do_configure_prepend() {
    install -d ${D}${datadir}/cmake/Modules
    install -m 0644 ${S}/cmake/Findnl-3.cmake ${D}${datadir}/cmake/Modules/
    install -m 0644 ${S}/cmake/Findnl-genl-3.cmake ${D}${datadir}/cmake/Modules/
}

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
