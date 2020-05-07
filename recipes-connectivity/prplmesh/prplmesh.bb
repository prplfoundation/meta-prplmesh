# SPDX-License-Identifier: BSD-2-Clause-Patent
# SPDX-FileCopyrightText: 2020 the prplMesh contributors (see AUTHORS.md)
# This code is subject to the terms of the BSD+Patent license.
# See LICENSE file for more details.

inherit cmake pythonnative

S = "${WORKDIR}/git"
OECMAKE_SOURCEPATH = "${S}/"

SECTION = "net"
LICENSE = "BSD-2-Clause-Patent.txt"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7b45146d47e73bcbac1068e5cfc2a9fb"
DEPENDS = "python-native python-pyyaml-native json-c openssl zeromq"
RDEPENDS_${PN} = "iproute2 busybox readline"
SUMMARY = "prplMesh"

SRCREV = "911392026280d9961823a9359e6cc4f08c9ee0a2"
SRC_URI = "git://github.com/prplfoundation/prplMesh.git;protocol=http;branch=master"

BUILD_TYPE ?= "Release"
TARGET_PLATFORM ?= "linux"
BWL_TYPE ?= "DUMMY"
INSTALL_PREFIX ?= "/opt/prplmesh"

EXTRA_OECMAKE = "-DTARGET_PLATFORM=${TARGET_PLATFORM} \
                 -DBWL_TYPE=${BWL_TYPE} \
                 -DUSE_USER_TMP_PATH=0 \
                 -DCMAKE_INSTALL_PREFIX:PATH=${INSTALL_PREFIX} \
                 -DCMAKE_BUILD_TYPE=${BUILD_TYPE} \
                 -DMSGLIB=zmq"

do_install_append() {
    install -d ${D}${datadir}/cmake/Modules/
    mv ${D}${INSTALL_PREFIX}/lib/cmake/* ${D}${datadir}/cmake/Modules/
    rmdir ${D}${INSTALL_PREFIX}/lib/cmake

    rm -rf ${D}${INSTALL_PREFIX}/host
}

# Explicit add paths to "*.so" files will be not needed when recipe start to
# build prplMesh almost from commit with "mapfcommon" library name fix
# See detais in PR https://github.com/prplfoundation/prplMesh/pull/1256

FILES_${PN} += "\
    ${INSTALL_PREFIX}/bin \
    ${INSTALL_PREFIX}/lib/lib*.so.* \
    ${INSTALL_PREFIX}/lib/mapfcommon.so.* \
    ${INSTALL_PREFIX}/share \
    ${INSTALL_PREFIX}/scripts \
    ${INSTALL_PREFIX}/config \
"

FILES_${PN}-dev += "\
    ${INSTALL_PREFIX}/include \
    ${INSTALL_PREFIX}/lib/*.so \
    ${datadir}/cmake/Modules \
"
