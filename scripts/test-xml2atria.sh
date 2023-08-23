#!/bin/sh
#
# Tests xml2atria on various XML files.
#
# Copyright (C) 2004 by James MacKay.
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

# Configuration
SCRIPTS_DIR="$(pwd)/$(dirname "$0")"
CMD="${SCRIPTS_DIR}/xml2atria"
DIRS="${SCRIPTS_DIR}/../src/com/steelcandy/plack/atria/programs/testing/expected-results"
XML_EXT=.xml
ATRIA_EXT=.atria
TEST_DIR=~/tmp/atria

(mkdir -p "${TEST_DIR}" && cd "${TEST_DIR}"
 echo "Putting test results under $(pwd)"
 for d in ${DIRS}
 do
     for f in "$d"/*${XML_EXT}
     do
         g="$(basename "$f" "${XML_EXT}")"
         echo "+++ $f"
         ${CMD} "$f" > "$g${ATRIA_EXT}"
     done
 done)
