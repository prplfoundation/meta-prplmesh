## HOWTO build

* fetch sources
```
git clone -b morty git://git.yoctoproject.org/poky.git
git clone -b morty git://git.openembedded.org/meta-openembedded.git
git clone -b morty git@github.com:prplfoundation/meta-prplmesh.git

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

## HOWTO run

* Run QEMU
```
runqemu tmp/deploy/images/qemux86-64 nographic
```
* Run prplmesh inside
```
prplmesh_utils.sh -p -v -D eth0 -C eth0 start
```
