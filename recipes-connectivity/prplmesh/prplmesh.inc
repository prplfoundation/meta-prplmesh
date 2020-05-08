# SPDX-License-Identifier: BSD-2-Clause-Patent
# SPDX-FileCopyrightText: 2020 the prplMesh contributors (see AUTHORS.md)
# This code is subject to the terms of the BSD+Patent license.
# See LICENSE file for more details.

inherit cmake pythonnative

S = "${WORKDIR}/git"
OECMAKE_SOURCEPATH = "${S}/"

SECTION = "net"
LICENSE = "BSD-2-Clause-Patent"
DEPENDS = "python-native python-pyyaml-native json-c readline openssl"
RDEPENDS_${PN} = "iproute2 busybox"
SUMMARY = "prplMesh"

SRC_URI = "git://github.com/prplfoundation/prplMesh.git;protocol=http;branch=master"

INSANE_SKIP_${PN} = "useless-rpaths"

BUILD_TYPE ?= "Release"
TARGET_PLATFORM ?= "linux"
BWL_TYPE ?= "DUMMY"
INSTALL_PREFIX ?= "/opt/prplmesh"

EXTRA_OECMAKE = "-DTARGET_PLATFORM=${TARGET_PLATFORM} \
                 -DBWL_TYPE=${BWL_TYPE} \
                 -DUSE_USER_TMP_PATH=0 \
                 -DCMAKE_INSTALL_PREFIX:PATH=${INSTALL_PREFIX} \
                 -DPLATFORM_INCLUDE_DIR=${STAGING_INCDIR} \
                 -DPLATFORM_BUILD_DIR=${TMPDIR}/work/${MULTIMACH_TARGET_SYS} \
                 -DCMAKE_BUILD_TYPE=${BUILD_TYPE} \
                 -DMSGLIB=None"

do_install_append() {
    install -d ${D}${datadir}/cmake/Modules/
    mv ${D}${INSTALL_PREFIX}/lib/cmake/* ${D}${datadir}/cmake/Modules/
    rmdir ${D}${INSTALL_PREFIX}/lib/cmake

    rm -rf ${D}${INSTALL_PREFIX}/host
}

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
