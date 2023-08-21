#!/bin/bash
#
# $Id: run-tests.sh,v 1.1 2005/10/21 13:21:27 jgm Exp $
#
# Cleans up the testing directory and its subdirectories.
#
# Author: James MacKay
#
# Copyright (C) 2005 by James MacKay.
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

BASE_DIR=$(dirname $0)

SRC_DIR=source
EXPECTED_RESULTS_DIR=expected-results
ACTUAL_RESULTS_DIR=actual-results

PROGRAM=atria2xml
SRC_EXT=.atria
DEST_EXT=.xml


# Arguments processing

PROG_NAME=$(basename $0)

if [ $# -ne 0 ]
then
    cat >&2 <<EOF

usage: ${PROG_NAME}

EOF
    exit 1
fi


# Functions

# Cleans up any files that might have been left behind by testing.
function cleanupFiles() {
    rm -f ${ACTUAL_RESULTS_DIR}/*${DEST_EXT}
}

# Main program

(cd "${BASE_DIR}" && cleanupFiles)
