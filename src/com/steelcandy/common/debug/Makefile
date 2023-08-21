###########################################################################
#
# Makefile for the Assertions Java package.
#
# Copyright (C) 1999 by James MacKay.
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
###########################################################################

# General
RM=rm -f

# Backup/Archiving
ZIP=zip
ARCHIVE=jassert.zip
ARCH_FILES= Makefile *.java *.txt *.html
BACKUP_DIR=${HOME}/backup

# Java
JAVAC=javac
JAVADOC=javadoc

classes:
	${JAVAC} *.java

docs:
	${JAVADOC} *.java

all: classes docs

arch:
	${ZIP} ${ARCHIVE} ${ARCH_FILES}
	mv ${ARCHIVE} ${BACKUP_DIR}

clean:
	-${RM} *~ *.zip

realclean: clean
	-${RM} *.class
