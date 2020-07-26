<!--
SPDX-License-Identifier: BSD-2-Clause-Patent
Copyright (c) 2020 the prplMesh contributors
This code is subject to the terms of the BSD+Patent license.
See LICENSE file for more details.
-->

## HOWTO build Yocto Poky 2.2

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

## HOWTO build Yocto Poky 3.1

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
