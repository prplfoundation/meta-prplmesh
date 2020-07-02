# SPDX-License-Identifier: BSD-2-Clause-Patent
# SPDX-FileCopyrightText: 2020 the prplMesh contributors (see AUTHORS.md)
# This code is subject to the terms of the BSD+Patent license.
# See LICENSE file for more details.

DESCRIPTION = "Packages needed to support prplMesh"

LICENSE = "BSD-2-Clause-Patent.txt"
LIC_FILES_CHKSUM = "file://${FILE_DIRNAME}/${PN}/LICENSE;md5=0518d409dae93098cca8dfa932f3ab1b"

PR = "r0"

inherit packagegroup

RDEPENDS_${PN} = "\
	prplmesh \
	"
