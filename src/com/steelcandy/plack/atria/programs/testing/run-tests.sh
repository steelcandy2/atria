#!/bin/bash
#
# Runs all of the program tests.
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

isQuiet="no"
if [ $# -gt 0 ]
then
    if [ "x$1" = "x-q" ]
    then
        isQuiet="yes"
        shift
    fi
fi

if [ $# -ne 0 ]
then
    cat >&2 <<EOF

usage: [-q] ${PROG_NAME}

EOF
    exit 1
fi


# Functions

# Checks that the specified directory exists and is a directory.
#
# usage: checkDirectoryExists pathname description
#
function checkDirectoryExists() {
    path=$1
    desc=$2

    if [ ! -d "${path}" ]
    then
        if [ ! -f "${path}" ]
        then
            cat >&2 <<EOF

${PROG_NAME}: the ${desc} directory "${path}" doesn't exist

EOF
            exit 1
        else
            cat >&2 <<EOF

${PROG_NAME}: the ${desc} directory "${path}" isn't a directory

EOF
            exit 1
        fi
    fi
}

# Performs all of the tests
function performTests() {

    checkDirectoryExists "${SRC_DIR}" "source"
    checkDirectoryExists "${EXPECTED_RESULTS_DIR}" "expected results"
    checkDirectoryExists "${ACTUAL_RESULTS_DIR}" "actual results"

    let testNumber=0
    let numFailedTests=0
    for src in ${SRC_DIR}/*${SRC_EXT}
    do
        let testNumber=testNumber+1
        if [ "${isQuiet}" = "no" ]
        then
            echo ""
            echo "Test #${testNumber}: converting ${src} to XML"
        fi

        noext=$(basename "${src}" "${SRC_EXT}")
        dest="${noext}${DEST_EXT}"
        actual="${ACTUAL_RESULTS_DIR}/${dest}"
        expected="${EXPECTED_RESULTS_DIR}/${dest}"

        ${PROGRAM} "${src}" > "${actual}"
        exitCode=$?
        if [ $exitCode -eq 0 ]
        then
            diff -u "${actual}" "${expected}"
            if [ $? -ne 0 ]
            then
                let numFailedTests=numFailedTests+1
            fi
        else
            echo "'${PROGRAM}' failed: it exited with exit code ${exitCode}"
            let numFailedTests=numFailedTests+1
        fi
    done

    if [ "${isQuiet}" = "no" ]
    then
        echo ""
        if [ ${numFailedTests} -eq 0 ]
        then
            echo "All ${testNumber} tests succeeded"
        else
            echo "${numFailedTests} of ${testNumber} tests FAILED"
        fi
        echo ""
    fi

    exit ${numFailedTests}
}


# Main program

(cd "${BASE_DIR}" && performTests)
