# SPDX-License-Identifier: BSD-2-Clause-Patent
# SPDX-FileCopyrightText: 2020 the prplMesh contributors (see AUTHORS.md)
# This code is subject to the terms of the BSD+Patent license.
# See LICENSE file for more details.

DESCRIPTION = "Packages needed to support prplMesh"

LICENSE = "BSD-2-Clause-Patent.txt"
# LIC_FILES_CHKSUM = "file://LICENSE;md5=7b45146d47e73bcbac1068e5cfc2a9fb"
# LIC_FILES_CHKSUM = "file://BSD-2-Clause-Patent.txt;md5=0518d409dae93098cca8dfa932f3ab1b"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0518d409dae93098cca8dfa932f3ab1b"

PR = "r0"

inherit packagegroup

RDEPENDS_${PN} = "\
	prplmesh \
	"
