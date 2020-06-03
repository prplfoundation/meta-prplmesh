<!--
SPDX-License-Identifier: BSD-2-Clause-Patent
Copyright (c) 2020 the prplMesh contributors
This code is subject to the terms of the BSD+Patent license.
See LICENSE file for more details.
-->

## RDKB (OE 2.2)

## HOWTO build image for Raspberry Pi 3 B+

> Based on [official RDK build manual](https://wiki.rdkcentral.com/pages/viewpage.action?pageId=71011616#RDK-B(RaspberryPi3B+)BuildandSetupManual-RouterProfile-BuildInstructions)

* Fetch sources
```
repo init -u https://code.rdkcentral.com/r/manifests -b rdk-next -m rdkb-extsrc.xml
repo sync -j4 --no-clone-bundle
```

* Fetch meta-prplmesh
```
git clone -b rdkb-next https://github.com/prplfoundation/meta-prplmesh
```

* Source build env
```
MACHINE=raspberrypi-rdk-broadband source meta-cmf-raspberrypi/setup-environment 
```

* Append to file `conf/bblayers.conf`
```
BBLAYERS =+ "${RDKROOT}/meta-prplmesh"
```

* Run image build
```
bitbake rdk-generic-broadband-image
```

## Yocto Poky 2.2 Morty

### HOWTO build

* fetch sources
```
git clone -b morty https://git.yoctoproject.org/git/poky
git clone -b morty https://git.openembedded.org/meta-openembedded
git clone -b morty https://github.com/prplfoundation/meta-prplmesh

```
* init build env
```
cd poky
TEMPLATECONF=../meta-prplmesh/conf/poky source oe-init-build-env
```
* build image (QEMU x86_64 by default)
```
bitbake core-image-minimal
```

### HOWTO run QEMU

* Run QEMU
```
runqemu tmp/deploy/images/qemux86-64 nographic
```
* Login as "root" (no password)
* Run prplmesh inside
```
/opt/prplmesh/scripts/prplmesh_utils.sh -p -v -D eth0 -C eth0 start
```
* Check status
```
/opt/prplmesh/scripts/prplmesh_utils.sh status
```
