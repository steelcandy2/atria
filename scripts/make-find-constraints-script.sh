#!/bin/sh
#
# Given on its standard input the names of all of a language's validity
# constraints as they appear in the language's automatically generated
# constraint-names.txt file, this script outputs on its standard output a
# script that finds all occurrences of the validity constraint names -
# in both their "method" and "constant forms - in all of the Java source
# files in and under what is the current directory when the script is run.
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


# The files to exclude from method and constant searches
excludesFilter=""
#echo "++++ excludes filter = $excludesFilter"


# Output header, function definitions, etc.
cat <<START
#!/bin/sh
#
# Finds all occurrences of the validity constraint names, both as constants and
# as they occur in the names of violated...() methods.
#
# This script was automatically generated by the script
# $0
#
# Author: James MacKay
#


#
# Configuration
#

# The directory in and under which to look for source code files to search
DIR="."

# The extension that a file will have iff it is a source file to search
EXT=".java"


#
# Function definitions
#

function findMethod {
    find \$DIR -type f -name "*\$EXT" | xargs grep -ln "violated\$1" $excludesFilter | awk '{ print "    " \$0 " [method]" }'
}

function findConstant {
    find \$DIR -type f -name "*\$EXT" | xargs grep -ln "\$1" $excludesFilter | awk '{ print "    " \$0 " [constant]" }'
}


#
# Check for each constraint name
#

START


# Output the code that checks for each constraint name
while [ true ]
do
    read name
    if [ $? -ne 0 ]
    then
        exit 0
    fi

    methodName=$(echo $name | sed 's#\.##')
    constantName=$(echo $methodName | sed 's#[A-Z]#_\0#g' | sed 's#^.##' | tr '[a-z]' '[A-Z]')
    # echo "$name - $methodName - $constantName"

    cat <<EOF
echo "===> $name"
findMethod $methodName
findConstant $constantName
EOF
done
