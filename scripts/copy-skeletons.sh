#!/bin/sh
#
# Copies all of the *.skeleton files in the current directory to the
# files they're skeletons of.
#
# Copyright (C) 2003-2023 by James MacKay.
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

for f in *.skeleton
do
    g="$(basename "$f" .skeleton)"
    cp -f "$f" "$g"
    chmod u+w "$g"
done
