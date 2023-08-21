# The makefile for the top-level plack directory.
#
# Copyright (C) 2001 by James MacKay.
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

include general.makefile


#
# Files and directories.
#

# All of the subdirectories of the current directory.
SUBDIRS:=$(CREATE_SUBDIRS_LIST)

# Archiving/backup.
ARCHIVE_BASE:=current4
ARCHIVE:=$(ARCHIVE_BASE).tgz
ENCRYPTED_ARCHIVE:=$(ARCHIVE_BASE).pgp
CVS_REPO_DIR:=$(CVSROOT)/steelcandy
CVS_PLACK_LIB_DIR:=./steelcandy/plack/lib
CVS_MODULE_NAME:=plack

# The files in this directory (only) that are to be backed up.
WHAT_TO_BACKUP= *.sh $(MAKEFILE_NAME) *.makefile *.template *.txt


#
# Rules
#

.PHONY: classes all
.PHONY: docs javadocs
.PHONY: clean realclean
.PHONY: arch narch arch_here narch_here encrypt create-backup-list


# make all of the packages under this directory
classes:
	$(MAKE_SUBDIR_CLASSES)

all: javadocs
	$(MAKE_SUBDIR_ALL)


docs:
	$(MAKE_SUBDIR_DOCS)

# generate all of the javadocs
javadocs: javadocs-from-packages

# generate the javadocs for all packages at once
#
# NOTE: the package javadocs currently aren't used (and thus
#       the make-package-javadocs dependency is commented out).
javadocs-from-packages: # make-package-javadocs
	$(FIND) .. -type d -mindepth 2 -path '../plack/*' -not -path '*/CVS*' | $(SED) -e s#^\.\./## -e s#/#.#g | $(XARGS) $(JAVADOC) $(JDOCFLAGS)

# generates javadocs for all source files at once
javadocs-from-sources: # make-package-javadocs
	$(FIND) . -name '*.java' | $(SED) s#^./## | $(XARGS) $(JAVADOC) $(JDOCFLAGS)
	$(RM) $(TMP_JAVA_SRC_LIST)


# Backs up all important files, encrypts them, and puts them in
# the backup directory.
encrypt: arch_here
	$(PGPE) -r $(ENCRYPT_RECIPIENT) -o $(ENCRYPTED_ARCHIVE) $(ARCHIVE)
	$(RM) $(ARCHIVE)
	$(MV) $(ENCRYPTED_ARCHIVE) $(BACKUP_DIR)

# Backs up all important files and puts them in the backup directory.
arch: arch_here
	$(MV) $(ARCHIVE) $(BACKUP_DIR)

# Backs up all important files nicely and puts them in the backup directory.
narch: narch_here
	$(MV) $(ARCHIVE) $(BACKUP_DIR)

# Backs up all important files, leaving them in the current directory.
arch_here:
	(cd $(CVSROOT) && $(TAR) cz --exclude $(CVS_PLACK_LIB_DIR) .) > $(ARCHIVE)

#arch_here: create-backup-list
#	$(CAT) $(TMP_BACKUP_LIST) | $(STDIN_ZIP) $(ARCHIVE)
#	-$(RM) $(TMP_BACKUP_LIST)

# Backs up all important files nicely, leaving them in the current directory.
narch_here:
	$(NICE) (cd $(CVS_REPO_DIR) && $(TAR) cz $(CVS_MODULE_NAME)) > $(ARCHIVE)

#narch_here: create-backup-list
#	$(NICE) ($(CAT) $(TMP_BACKUP_LIST) | $(STDIN_ZIP) $(ARCHIVE))
#	-$(RM) $(TMP_BACKUP_LIST)

# Creates a file containing the names of all of the files to back up.
create-backup-list:
	$(RELATIVE_TO_TOP) $(WHAT_TO_BACKUP) > $(TMP_BACKUP_LIST)
	$(MAKE_SUBDIR_APPEND_TO_BACKUP_LIST)
