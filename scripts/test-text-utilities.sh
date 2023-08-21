#!/bin/sh
#
# Runs the Java class com.steelcandy.common.text.TextUtilities.
#
# Copyright (C) 2003-2010 by James MacKay.
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

# PLACK_BASE must be set.
if [ "x${PLACK_BASE}" = "x" ]
then
    cat >&2 <<EOF

PLACK_BASE must be set.

EOF
    exit 1
fi

# JAVA_HOME must be set.
if [ "x${JAVA_HOME}" = "x" ]
then
    cat >&2 <<EOF

JAVA_HOME must be set.

EOF
    exit 2
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
LIB_DIR=${PLACK_BASE}/java/lib

# The directory containing non-plack, non-JDK jars.
EXTERNAL_LIB_DIR=/usr/local/lib/java

# Add the application-specific jars and classes to the classpath.
CP=${LIB_DIR}/steelcandy.jar:${LIB_DIR}/plack.jar:${LIB_DIR}/testing.jar:${EXTERNAL_LIB_DIR}/assert.zip:${EXTERNAL_LIB_DIR}/jdom.jar:${CP}

# The class to run
CLASS_NAME=com.steelcandy.common.text.TextUtilities

# Run the class
${JAVA} -classpath ${CP} ${CLASS_NAME} $*
