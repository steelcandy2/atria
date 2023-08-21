#!/bin/sh
#
# Outputs a list of all of the files in the current directory with the
# specified extension to the standard output with one filename per line.
# All of the filenames are relative to the current directory.
#
# The extension must be specified WITH any leading period.
#
# usage: src-list.sh extension
#
# Copyright (C) 2001 by James MacKay.
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <https://www.gnu.org/licenses/>.
#


if [ $# -ne 1 ]
then
    echo "usage: `basename $0` extension"
    echo
    echo "where extension is the extension that all files that"
    echo "will be output by this command must have."
    echo
    exit 1
fi

extension=$1
shift

# We remove lines equal to '*.${extension}' since presumably they're
# just the unexpanded filename pattern, indicating there are
# no Java source files in the current directory.
pattern="^*${extension}$"
for f in *${extension}
do
    echo $f
done | grep -v "$pattern"
exit 0
