#!/bin/sh
#
# Runs the Java class com.steelcandy.common.io.RecordingIndentWriter.
#
# Copyright (C) 2002 by James MacKay.
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

# JAVA_HOME must be set.
if [ "x${JAVA_HOME}" = "x" ]
then
    echo >&2
    echo "JAVA_HOME must be set." >&2
    echo >&2
    exit 1
fi

# If JRE_HOME isn't set then we set it based on JAVA_HOME.
if [ "x${JRE_HOME}" = "x" ]
then
    JRE_HOME=${JAVA_HOME}/jre
fi

# The Java interpreter to use to run the class.
JAVA=${JRE_HOME}/bin/java

# Initialize the classpath with the core jars.
JRE_LIB=${JRE_HOME}/lib
JAVA_LIB=${JAVA_HOME}/lib
CP=${JRE_LIB}/rt.jar:${JRE_LIB}/i18n.jar:${JAVA_LIB}/tools.jar

# The directory containing most/all of the jars.
LIB_DIR=/home/jgm/plack/lib

# Add the application-specific jars and classes to the classpath.
CP=${LIB_DIR}/plack.jar:/home/jgm/plack/lib/plack.jar:/home/jgm/plack/lib/assert.zip:/home/jgm/plack/lib/jdom.jar:${CP}

# The class to run
CLASS_NAME=com.steelcandy.common.io.RecordingIndentWriter

# Run the class
${JAVA} -classpath ${CP} ${CLASS_NAME} $*
