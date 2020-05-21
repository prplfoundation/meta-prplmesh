FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI_append = " file://FindNL.cmake \
                   file://FindLibNL.cmake \
                 "

do_install_append() {
    install -d ${D}${datadir}/cmake/Modules
    # install -m 0644 ${WORKDIR}/FindNL.cmake ${D}${datadir}/cmake/Modules
    install -m 0644 ${WORKDIR}/FindLibNL.cmake ${D}${datadir}/cmake/Modules
}
