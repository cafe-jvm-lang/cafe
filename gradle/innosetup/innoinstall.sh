#!/bin/bash
set -x

rm -rf /tmp/inno
mkdir /tmp/inno
cd /tmp/inno

wget -O is.exe http://files.jrsoftware.org/is/5/isetup-5.5.5.exe
innoextract is.exe
mkdir -p ~/".wine/drive_c/inno"
cp -R app/* ~/".wine/drive_c/inno"
