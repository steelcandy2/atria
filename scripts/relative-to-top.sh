#!/bin/sh
#
# Converts the specified list of filenames that are relative to the
# current directory to be relative to the specified top-level directory,
# and outputs the converted filenames to its standard output, one to a
# line. It is assumed that the current directory is a subdirectory of
# the top-level directory.
#
# usage: relative-to-top.sh top-level-dir filename [filename ...]
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

if [ $# -lt 2 ]
then
    echo "usage: `basename $0` top-level-dir filename [filename ...]"
    echo
    echo "where top-level-dir is the top-level directory to which the"
    echo "filenames are to be converted to be relative to, and the"
    echo "filenames are the filenames to be converted."
    echo
    exit 1
fi

topDir="$1"
shift

# The second sed strips off any leading slash in case the top-level
# directory was specified without a trailing slash.
for f in "$@"
do
    echo "${PWD}/$f"
done | sed "s#^${topDir}##" | sed 's#^/##'
