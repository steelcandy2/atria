#!/bin/sh
#
# Creates a script in this script directory with the specified name that runs
# the Java class with the specified full-qualified class name.
#
# usage: create-java-script.sh script-name class-name
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

# The scripts directory, which is assumed to contain this script.
SCRIPTS_DIR="$(dirname "$0")"

ANT=ant
ANT_OPTS="-find build.xml"

CREATE_SCRIPT_TARGET="create-script"


# Argument processing

if [ $# -ne 2 ]
then
    cat <<EOF

usage: $(basename $0) script-name class-name

where 'script-name' is the name of the script to generate, and 'class-name'
is the fully-qualified class name of the Java class that the script is to
run. The script that is created will be placed in this directory, so
'script-name' should not contain any directory parts.

EOF
    exit 1
fi

SCRIPT_NAME="$1"
CLASS_NAME="$2"


# Main program

SCRIPT_PATHNAME="${PWD}/${SCRIPTS_DIR}/${SCRIPT_NAME}"
echo
echo "Creating the script ${SCRIPT_PATHNAME} ..."
echo

"${ANT}" $ANT_OPTS \
         -Dscript="${SCRIPT_PATHNAME}" -Dclass="${CLASS_NAME}" \
         "${CREATE_SCRIPT_TARGET}"
