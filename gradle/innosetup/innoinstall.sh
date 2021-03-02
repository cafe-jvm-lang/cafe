#!/bin/bash
set -x

rm -rf /tmp/inno
mkdir /tmp/inno
cd /tmp/inno

wget -O is.exe https://jrsoftware.org/download.php/is.exe
innoextract is.exe
mkdir -p ~/".wine/drive_c/inno"
cp -R app/* ~/".wine/drive_c/inno"
