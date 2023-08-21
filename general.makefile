# A makefile defining common variables and rules. It is only
# intended to be included in other makefiles: it shouldn't be
# used as a standalone makefile.
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

export TOP_DIR:=$(CURDIR)
export JAVA_DIR:=$(TOP_DIR)
export SRC_DIR:=$(JAVA_DIR)/src
export CLASSES_DIR:=$(JAVA_DIR)/classes
export LIB_DIR:=$(JAVA_DIR)/lib
export JAVADOCS_DIR:=$(JAVA_DIR)/javadocs

PATH:=$(TOP_DIR):/bin:/usr/bin:/usr/local/bin:/usr/local/java/bin


#
# Sequence definitions
#

# The sequence of commands that executes a make rule in each directory
# in SUBDIRS that has a makefile in it. The __RULE_NAME part must be
# replaced by the make rule's name before this sequence can be used,
# so this sequence is usually used in the following manner:
#
#   $(subst __RULE_NAME, classes, $(subdir-make))
#
define Subdir-Make
for sdir in $(SUBDIRS); do if [ -f $$sdir/$(MAKEFILE_NAME) ]; then $(MAKE) -C $$sdir __RULE_NAME; fi; done
endef


#
# Programs
#
AWK:=awk
CAT:=cat
CHMOD:=chmod
DIR:=ls -l
ECHO:=echo
FIND:=find
GREP:=grep
LS:=ls
MV:=mv
NICE:=nice -n 19
PGPE:=pgpe
RM:=rm -f
RZIP:=zip -r # recursive zip
SED:=sed
STDIN_ZIP:=zip -@ # zip that gets its list of files from stdin
SYMLINK:=ln -s
SYMLINK_FORCE:=ln -sf
TAR:=tar
XARGS:=xargs
ZIP:=zip

#
# The names of the makefiles in a directory.
#
MAKEFILE_NAME:=Makefile
LOCAL_MAKEFILE_NAME:=Makefile.local

#
# Scripts
#
RELATIVE_TO_TOP:=relative-to-top.sh $(TOP_DIR)
CREATE_SUBDIRS_LIST:=$(shell $(DIR) | $(GREP) '^d' | $(AWK) '{print $$9}')

CREATE_SRC_LIST:=src-list.sh
CREATE_JAVA_SRC_LIST:=$(CREATE_SRC_LIST) .java

#
# Java
#
JAVAC:=javac
JPATH_OPT:=-classpath $(HOME)/src:$(HOME)/src/java/assert.zip:$(CLASSPATH)
JCLASSDIR_OPT:=
#JCLASSDIR_OPT:=-d $(CLASSES_DIR)
JFLAGS:=$(JPATH_OPT) -g $(JCLASSDIR_OPT)
TMP_JAVA_SRC_LIST:=.tmp-java-src-list

JAVADOC:=javadoc
JDOCFLAGS:=$(JPATH_OPT) -d $(JAVADOCS_DIR) -version -author -private


#
# Backup and cleanup
#
TMP_BACKUP_LIST:=$(TOP_DIR)/.tmp-backup-list
BACKUP_DIR:=$(HOME)/backup

CLEANUP_FILES=*~ *.tmp
REAL_CLEANUP_FILES=$(CLEANUP_FILES) $(TMP_JAVA_SRC_LIST)

ENCRYPT_RECIPIENT=jmackay

#
# CVS
#
CVSIGNORE_FILE=.cvsignore
LOCAL_CVS_IGNORE_FILE:=.cvsignore.local

#
# Commands to execute specific make rules in each subdirectory in
# SUBDIRS that has a makefile. (See Subdir-Make.)
#
MAKE_SUBDIR_CLASSES=$(subst __RULE_NAME, classes, $(Subdir-Make))
MAKE_SUBDIR_ALL=$(subst __RULE_NAME, all, $(Subdir-Make))
MAKE_SUBDIR_DOCS=$(subst __RULE_NAME, docs, $(Subdir-Make))
MAKE_SUBDIR_JAVADOCS=$(subst __RULE_NAME, javadocs, $(Subdir-Make))
MAKE_SUBDIR_PACKAGE_JAVADOCS=$(subst __RULE_NAME, make-package-javadocs, $(Subdir-Make))
MAKE_SUBDIR_CLEAN=$(subst __RULE_NAME, clean, $(Subdir-Make))
MAKE_SUBDIR_REALCLEAN=$(subst __RULE_NAME, realclean, $(Subdir-Make))
MAKE_SUBDIR_APPEND_TO_BACKUP_LIST=$(subst __RULE_NAME, append-to-backup-list, $(Subdir-Make))
MAKE_SUBDIR_GENERATE_JAVA_SRC=$(subst __RULE_NAME, java-src, $(Subdir-Make))
MAKE_SUBDIR_CVSIGNORE=$(subst __RULE_NAME, cvsignore, $(Subdir-Make))

#
# Implicit rules
#

# Builds class files from Java source
%.class: %.java
	$(JAVAC) $(JFLAGS) $<


#
# Explicit rules
#

.PHONY: clean realclean backup
.PHONY: cvsignore cvsignore-norecurse
.PHONY: make-java-src-list


# Backs up the entire CVS tree in the background and using
# nohup. A log file will appear in the current directory that
# logs the progress of the backup: it is deleted if/when the
# backup has been successfully completed.
backup:
	backup.sh

# Cleans up all generated and temporary files in this directory
# and all subdirectories.
clean:
	-$(RM) $(CLEANUP_FILES)
	$(MAKE_SUBDIR_CLEAN)

realclean:
	-$(RM) $(REAL_CLEANUP_FILES)
	$(MAKE_SUBDIR_REALCLEAN)


# Generate the current directory's and all of its subdirectories'
# (and sub-subdirectories', etc.) .cvsignore files
cvsignore: cvsignore-norecurse
	$(MAKE_SUBDIR_CVSIGNORE)

# Generate the current directory's .cvsignore file
cvsignore-norecurse:
	-$(CAT) $(LOCAL_CVS_IGNORE_FILE) > $(CVSIGNORE_FILE)


# Builds a list of all of the Java source files in the current directory
# (but not those in directories UNDER the current directory).
make-java-src-list:
	$(CREATE_JAVA_SRC_LIST) > $(TMP_JAVA_SRC_LIST)
