---
Title: Primeros pasos Fedora 25
Date: 2017-3-30 2:07
Modified: 2017-3-30 2:07
Category: 
Tags: fedora, linux, consola, fedora 25 
Authors: procamora
Slug: primeros-pasos-fedora-25
Summary: 
Status:
---





# Que haces después de instalar Fedora 25


# Pasos iniciales

## Actualización de paquetes

Primero actualizamos el sistema y lo reiniciamos, posiblemente haya una gran cantidad de paquetes a actualizar, junto con la actualización del kernel

```bash
dnf update
reboot
```

## Añadir repositorio RPMFusion necesario para varios paquetes que instalaremos después. (contiene programas importantes y paquetes nonfree)

```bash
dnf install https://download1.rpmfusion.org/free/fedora/rpmfusion-free-release-$(rpm -E %fedora).noarch.rpm
```

## Paquetes y librerías que mas uso

```bash
dnf install kde-i18n-Spanish mythes-es hyphen-es autocorr-es hunspell-es langpacks-es aspell-es calligra-l10n-es man-pages-es-extra kde-l10n-es java-openjdk java-1.8.0-openjdk-javadoc fluid-soundfont-common meld android-tools cmake expect flac-libs encfs python-devel python3-devel samba wine youtube-dl wget curl iftop system-config-users
```


## Herramientas básicas de compilación

```bash
dnf install kernel-headers kernel-devel git make libxml2 libxml2-devel alsa-firmware pavucontrol mercurial dkms
```


# Compresión y descompresión:

```bash
dnf install p7zip p7zip-plugins zip unzip
```


## Codecs

```bash
dnf install gstreamer gstreamer1-libav gstreamer1-plugins-bad-free-extras gstreamer1-plugins-bad-freeworld gstreamer1-plugins-good-extras gstreamer1-plugins-ugly gstreamer-ffmpeg gstreamer-plugins-bad gstreamer-plugins-bad-free-extras  gstreamer-plugins-ugly ffmpeg ffmpeg-libs libmatroska xvidcore libva-vdpau-driver libvdpau libvdpau-devel gstreamer1-vaapi gstreamer1-plugins-base-tools mencoder
```


# Programas

## Programas básicos

```bash
dnf install VirtualBox vim yakuake libreoffice libreoffice-langpack-es gnome-disk-utility filezilla sqlitebrowser gimp vlc fritzing kdenlive
```


## dispositivos hp (impresora)

```bash
 dnf install hplip hplip-common libsane-hpaio hplip-gui
```


# insync

```bash
rpm --import https://d2t3ff60b2tol4.cloudfront.net/repomd.xml.key
echo "[insync]
name=insync repo
baseurl=http://yum.insynchq.com/fedora/$releasever/
gpgcheck=1
gpgkey=https://d2t3ff60b2tol4.cloudfront.net/repomd.xml.key
enabled=1
metadata_expire=120m" > /etc/yum.repos.d/insync.repo
dnf install insync insync-dolphin
```


# pandoc

```bash
dnf install pandoc texlive texlive texlive-latex texlive-xetex texlive-collection-latex texlive-collection-latexrecommended texlive-xetex-def texlive-collection-xetex
```


## Spotify

```bash
dnf config-manager --add-repo=http://negativo17.org/repos/fedora-spotify.repo
dnf install spotify-client
```


## atom

```bash
wget -O atom.x86_64.rpm https://atom.io/download/rpm
dnf install atom.x86_64.rpm
rm atom.x86_64.rpm
```


## Visual Studio Code

```bash
wget -O vscode.x86_64.rpm https://go.microsoft.com/fwlink/?LinkID=760867
dnf install vscode.x86_64.rpm
rm vscode.x86_64.rpm
```



## Skype

```bash
wget -O skypeforlinux-64.rpm https://go.skype.com/skypeforlinux-64.rpm
dnf install skypeforlinux-64.rpm
rm skypeforlinux-64.rpm
```


## teamviewer

```bash
wget -O teamviewer.i686.rpm https://download.teamviewer.com/download/teamviewer.i686.rpm
dnf install teamviewer.i686.rpm
rm teamviewer.i686.rpm
```


## dropbox

```bash
wget -O dropbox.fedora.x86_64.rpm https://www.dropbox.com/download?dl=packages/fedora/nautilus-dropbox-2015.10.28-1.fedora.x86_64.rpm
dnf install dropbox.fedora.x86_64.rpm
rm dropbox.fedora.x86_64.rpm
```


## gitkraken

```bash
programa="gitkraken-amd64.tar.gz"
wget -O $programa https://release.gitkraken.com/linux/gitkraken-amd64.tar.gz
mv $programa ~/Programas/ && cd ~/Programas/
tar -xvf $programa && rm $programa && cd -
```


## pycharm

```bash
programa="pycharm-community-2017.1.tar.gz"
wget -O $programa https://download.jetbrains.com/python/pycharm-community-2017.1.tar.gz
mv $programa ~/Programas/ && cd ~/Programas/
tar -xvf $programa && rm $programa && cd -
```


## telegram

```bash
programa="telegram.tar.xz"
wget -O $programa https://tdesktop.com/linux
mv $programa ~/Programas/ && cd ~/Programas/
tar -xvf $programa && rm $programa && cd -
```


## eclipse

```bash
programa="eclipse-inst-linux64.tar.gz"
wget -O $programa http://mirror.ibcp.fr/pub/eclipse//oomph/epp/neon/R2a/eclipse-inst-linux64.tar.gz
mv $programa ~/Programas/ && cd ~/Programas/
tar -xvf $programa && rm $programa && cd -
```


## arduino

```bash
programa="arduino-linux64.tar.xz"
wget -O $programa http://download.arduino.org/IDE/1.8.1/arduino-1.8.1-linux64.tar.xz
mv $programa ~/Programas/ && cd ~/Programas/
tar -xvf $programa && rm $programa && cd -
```


## rpm

https://www.google.com/chrome/browser/desktop/index.html

https://www.googleplaymusicdesktopplayer.com/#!

