# SPDX-License-Identifier: BSD-2-Clause-Patent
# SPDX-FileCopyrightText: 2020 the prplMesh contributors (see AUTHORS.md)
# This code is subject to the terms of the BSD+Patent license.
# See LICENSE file for more details.

inherit cmake pythonnative

S = "${WORKDIR}/git"
OECMAKE_SOURCEPATH = "${S}/"

SECTION = "net"
LICENSE = "BSD-2-Clause-Patent.txt"
# LIC_FILES_CHKSUM = "file://LICENSE;md5=7b45146d47e73bcbac1068e5cfc2a9fb"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0518d409dae93098cca8dfa932f3ab1b"
# DEPENDS = "python-native python-pyyaml-native json-c openssl nanomsg"
# DEPENDS = "python-native python-pyyaml-native json-c openssl wpa-supplicant libnl"
DEPENDS = "python-native python-pyyaml-native json-c openssl nng wpa-supplicant libnl"
RDEPENDS_${PN} = "iproute2 busybox readline"
SUMMARY = "prplMesh"

# SRCREV = "911392026280d9961823a9359e6cc4f08c9ee0a2"
SRCREV = "d9eb170b69c1c225e40df5c191e8978568ec2e08"
SRC_URI = "git://github.com/prplfoundation/prplMesh.git;protocol=http;branch=master \
           file://001-add_find_nl_for_nl80211_bwl_type.patch"

BUILD_TYPE ?= "Release"
TARGET_PLATFORM ?= "linux"
# BWL_TYPE ?= "DUMMY"
# BWL_TYPE ?= "DWPAL"
BWL_TYPE ?= "NL80211"
INSTALL_PREFIX ?= "/opt/prplmesh"

EXTRA_OECMAKE = "-DTARGET_PLATFORM=${TARGET_PLATFORM} \
                 -DBWL_TYPE=${BWL_TYPE} \
                 -DUSE_USER_TMP_PATH=0 \
                 -DCMAKE_INSTALL_PREFIX:PATH=${INSTALL_PREFIX} \
                 -DCMAKE_BUILD_TYPE=${BUILD_TYPE} \
                 -DCMAKE_VERBOSE_MAKEFILE:BOOL=ON \
                 -DCMAKE_INCLUDE_PATH=${STAGING_INCDIR} \
                 -DCMAKE_LIBRARY_PATH=${STAGING_LIBDIR} \
                 -DMSGLIB=nng"
#                 -DMSGLIB=None"
# CMake could not found CMake module for find nanomsg lib
# So could not compile with it
#                 -DMSGLIB=nng
# Didn't help to force cmake find libnl header
#                 -DCMAKE_CXX_FLAGS=-I${STAGING_INCDIR}
# Not helped too
#                 -DCMAKE_FIND_ROOT_PATH_MODE_INCLUDE=ONLY

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
