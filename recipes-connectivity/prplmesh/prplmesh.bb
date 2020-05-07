inherit cmake pythonnative

S = "${WORKDIR}/git"
OECMAKE_SOURCEPATH = "${S}/"

SECTION = "net"
LICENSE = "BSD-2-Clause-Patent"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7b45146d47e73bcbac1068e5cfc2a9fb"
DEPENDS = "python-native python-pyyaml-native json-c readline openssl"
RDEPENDS_${PN} = "iproute2 busybox"
SUMMARY = "prplMesh"

SRCREV = "911392026280d9961823a9359e6cc4f08c9ee0a2"
SRC_URI = "git://github.com/prplfoundation/prplMesh.git;protocol=http;branch=master"

INSANE_SKIP_${PN} = "useless-rpaths"

BUILD_TYPE ?= "Release"
TARGET_PLATFORM ?= "linux"
BWL_TYPE ?= "DUMMY"

EXTRA_OECMAKE = "-DTARGET_PLATFORM=${TARGET_PLATFORM} \
                 -DBWL_TYPE=${BWL_TYPE} \
                 -DUSE_USER_TMP_PATH=0 \
                 -DPLATFORM_INCLUDE_DIR=${STAGING_INCDIR} \
                 -DPLATFORM_BUILD_DIR=${TMPDIR}/work/${MULTIMACH_TARGET_SYS} \
                 -DCMAKE_BUILD_TYPE=${BUILD_TYPE} \
                 -DMSGLIB=None"

do_install_append() {
    install -d ${D}${sysconfdir}/prplmesh
    install -m 0644 ${D}${datadir}/prplmesh*db* ${D}${sysconfdir}/prplmesh/

    install -d ${D}/tmp/root
    install -d ${D}/tmp/beerocks/logs
}

FILES_${PN} += "\
    ${bindir} \
    ${libdir}/mapfcommon.so.* \
    /usr/share \
    /usr/scripts \
    /usr/config \
    /tmp \
"

FILES_${PN}-dev += "\
    ${nonarch_libdir}/cmake \
    ${includedir} \
    ${libdir}/*.so \
    /usr/host \
"
