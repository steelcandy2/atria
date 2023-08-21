#!/bin/sh
#
# Repeat parser tests until one fails.
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

SRC_FILE=source-code.atria

done=0
echo         "**** Started tests @ [`date`]"
while [ $done -eq 0 ]
do
    ant -emacs -s build.xml atria-parser-test 2> err.tmp > out.tmp
    exitCode=$?
    D=`date`
    SIZE=`ls -l $SRC_FILE | awk '{print $5}'`
    if [ $exitCode -ne 0 ]
    then
        echo "===> parser test exited with exit code $exitCode [$D]: $SIZE bytes"
        cat out.tmp
        done=1
    else
        echo "---> successful test [$D]: $SIZE bytes"
    fi
done
