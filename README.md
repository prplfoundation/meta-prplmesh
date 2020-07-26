<!--
SPDX-License-Identifier: BSD-2-Clause-Patent
Copyright (c) 2020 the prplMesh contributors
This code is subject to the terms of the BSD+Patent license.
See LICENSE file for more details.
-->

# RDKB (OE 2.2)

## Raspberry Pi 3 B+

### HOWTO build

> Based on [official RDK build manual](https://wiki.rdkcentral.com/pages/viewpage.action?pageId=71011616#RDK-B(RaspberryPi3B+)BuildandSetupManual-RouterProfile-BuildInstructions)

* Fetch sources
```
repo init -u https://code.rdkcentral.com/r/manifests -b rdk-next -m rdkb-extsrc.xml
repo sync -j4 --no-clone-bundle
```

* Fetch meta-prplmesh
```
git clone -b rdk-next https://github.com/prplfoundation/meta-prplmesh
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

### HOWTO run

* Run on board after boot
```
hostapd -B /usr/ccsp/wifi/hostapd0.conf
/opt/prplmesh/scripts/prplmesh_utils.sh start
```
* Check status
```
/opt/prplmesh/scripts/prplmesh_utils.sh status
```

# RDKB (OE 3.1)

## Turris Omnia

Based on [RDK Central Wiki](https://wiki.rdkcentral.com/display/RDK/Yocto-3.1+rdk-generic-broadband-image+Turris+Omnia)

## HOWTO build

* Fetch sources
```
repo init -u https://code.rdkcentral.com/r/manifests -m rdkb-turris-extsrc.xml  -b  yocto-dunfell-upgrade
repo sync -j4 --no-clone-bundle
```

* Fetch meta-prplmesh
```
git clone -b master https://github.com/prplfoundation/meta-prplmesh
```

* Source build env
```
MACHINE=turris source meta-turris/setup-environment
```

* Append to file `conf/bblayers.conf`
```
BBLAYERS =+ "${RDKROOT}/meta-prplmesh"
```

* Run image build
```
bitbake core-image-minimal
bitbake rdk-generic-broadband-image
```

# Yocto Poky 2.2 (Morty)

## QEMU

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

### HOWTO run

* Run QEMU by command
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

# Yocto Poky 3.1 Dunfell

## HOWTO build

* fetch sources
```
git clone -b dunfell https://git.yoctoproject.org/git/poky
git clone -b dunfell https://git.openembedded.org/meta-openembedded
git clone -b dunfell git://git.openembedded.org/meta-python2
git clone -b dunfell https://github.com/prplfoundation/meta-prplmesh

```
* init build env
```
cd poky
TEMPLATECONF=../meta-prplmesh/conf/poky-dunfell source oe-init-build-env
```
* build image (QEMU x86_64 by default)
```
bitbake core-image-minimal
```

## HOWTO run

* Run QEMU by command
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

