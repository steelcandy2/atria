# This makefile should be symlinked (or copied) as the Makefile
# for each and every directory (except the top-level directory).
# Rules and variables can be defined and overridden by specifying them
# in a makefile called Makefile.local in the current directory.
#
# Copyright (C) 2001-2003 by James MacKay.
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

.SUFFIXES:

TOP_DIR:=/extra/home/jgm/src/steelcandy/plack
include $(TOP_DIR)/general.makefile

# Include, if present, the rules defined in the current directory's
# local makefile.
-include $(LOCAL_MAKEFILE_NAME)


#
# Files and directories
#

# All of the subdirectories of the current directory.
SUBDIRS:=$(CREATE_SUBDIRS_LIST)

# By default back up all Java source files, Makefiles and text files. (Local
# makefiles can override this default, possibly using
# DEFAULT_WHAT_TO_BACKUP.)
WHAT_TO_BACKUP=$(DEFAULT_WHAT_TO_BACKUP)
DEFAULT_WHAT_TO_BACKUP=*.java *.properties $(MAKEFILE_NAME) $(LOCAL_MAKEFILE_NAME) *.txt *.template *.sh *.00? *.results *.errors

REAL_CLEANUP_FILES:=$(REAL_CLEANUP_FILES) *.class


#
# Rules
#

.PHONY: classes all docs javadocs
.PHONY: clean realclean arch narch encrypt
.PHONY: append-to-backup-list java-src

# compile all of the Java source in this directory and all
# subdirectories into class files.
classes: java-src make-java-src-list
	if [ -s $(TMP_JAVA_SRC_LIST) ]; then $(CAT) $(TMP_JAVA_SRC_LIST) | $(XARGS) $(JAVAC) $(JFLAGS); fi
	-$(RM) $(TMP_JAVA_SRC_LIST)
	$(MAKE_SUBDIR_CLASSES)

all: classes docs


docs:
	$(MAKE_SUBDIR_DOCS)

javadocs:
	$(MAKE) -C .. javadocs


# Archives everything in this source tree.
arch:
	$(MAKE) -C .. arch

# Archives everything in this source tree, but nicely.
narch:
	$(MAKE) -C .. narch

# encrypts the backup file
encrypt:
	$(MAKE) -C .. encrypt


# Appends to the TMP_BACKUP_LIST the filenames, relative to the top
# directory, of the files in this directory that are to be backed up.
append-to-backup-list:
	$(RELATIVE_TO_TOP) $(WHAT_TO_BACKUP) >> $(TMP_BACKUP_LIST)
	$(MAKE_SUBDIR_APPEND_TO_BACKUP_LIST)

# Generate Java source files.
java-src:
	$(MAKE_SUBDIR_GENERATE_JAVA_SRC)
