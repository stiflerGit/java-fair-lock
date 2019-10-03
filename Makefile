# A generic makefile for a Java project.

VERSION_NUMBER := 1.0

# Location of trees.
SOURCE_DIR  := src
OUTPUT_DIR  := classes
BIN_DIR := bin

# Unix tools
AWK         := awk
FIND        := /bin/find
MKDIR       := mkdir -p
RM          := rm -rf
SHELL       := /bin/bash

# Java tools
JAVA        := $(shell which java)
JAVAC       := $(shell which javac)

JFLAGS      := -sourcepath $(SOURCE_DIR) -d $(OUTPUT_DIR)                 

JVMFLAGS    := -ea -esa -Xfuture

JVM         := $(JAVA) $(JVMFLAGS)

JAR         := $(shell which jar)
JARFLAGS    := cvf

# Set the Java classpath
class_path := OUTPUT_DIR                

# space - A blank space
space := $(empty) $(empty)

# $(call build-classpath, variable-list)
define build-classpath
$(strip                                         \
	$(patsubst :%,%,                              \
	$(subst : ,:,                               \
	$(strip                                   \
	$(foreach j,$1,$(call get-file,$j):)))))
endef

# $(call get-file, variable-name)
define get-file
	$(strip $($1) \
	$(if $(call file-exists-eval,$1),,          \
	$(warning The file referenced by variable \
	'$1' ($($1)) cannot be found)))
endef

# $(call file-exists-eval, variable-name)
define file-exists-eval
	$(strip                                       \
	$(if $($1),,$(warning '$1' has no value))   \
	$(wildcard $($1)))
endef

# $(call file-exists, wildcard-pattern)
file-exists = $(wildcard $1)

# $(call check-file, file-list)
define check-file
	$(foreach f, $1,                              \
	$(if $(call file-exists, $($f)),,           \
	$(warning $f ($($f)) is missing)))
endef

# #(call make-temp-dir, root-opt)
define make-temp-dir
	$(shell mktemp -t $(if $1,$1,make).XXXXXXXXXX)
endef

# MANIFEST_TEMPLATE - Manifest input to m4 macro processor
MANIFEST_TEMPLATE := src/manifest/default.mf
TMP_JAR_DIR       := $(call make-temp-dir)
TMP_MANIFEST      := $(TMP_JAR_DIR)/manifest.mf

# $(call add-manifest, jar, jar-name, jar-main_class)
define add-manifest
  $(RM) $(TMP_JAR_DIR)
  $(MKDIR) $(TMP_JAR_DIR)
  m4 --define=SPEC_TITLE=$(notdir $1)\
  	--define=JAR_NAME=$(notdir $2)\
	--define=IMPL_VERSION=$(VERSION_NUMBER)\
	--define=MAIN_CLASS=$(strip $3)\
     $(abspath $(MANIFEST_TEMPLATE)) > $(TMP_MANIFEST)
  $(JAR) -ufm $(BIN_DIR)/$(notdir $1) $(TMP_MANIFEST)
  $(RM) $(dir $(TMP_MANIFEST))
endef

# $(call make-jar,jar-variable-prefix)
define make-jar
.PHONY: $1 $$($1_name)
$1: $($1_name)
	

$$($1_name):
	cd classes && \
	$(JAR) $(JARFLAGS) $(abspath $(BIN_DIR))/$$(notdir $$@) $$($1_packages)
	$$(call add-manifest, $$@, $$($1_name), $$($1_main_class))
endef

# Set the CLASSPATH
export CLASSPATH := $(call build-classpath, $(class_path))

# make-directories - Ensure output directory exists.
make-directories := $(shell $(MKDIR) $(OUTPUT_DIR))

# all - Perform all tasks for a complete build
.PHONY: all
all: compile jars

# all_javas - Temp file for holding source file list
all_javas := $(OUTPUT_DIR)/all.javas

# compile - Compile the source
.PHONY: compile
compile: $(all_javas)
	$(JAVAC) $(JFLAGS) @$<

# all_javas - Gather source file list
.INTERMEDIATE: $(all_javas)
$(all_javas):
	$(FIND) $(SOURCE_DIR) -name '*.java' > $@

# jar_list - List of all jars to create
jar_list := point1_jar point20_jar point21_jar point31_jar

.PHONY: jars $(jar_list)
jars: mk_bindir $(jar_list)

mk_bindir:
	@mkdir -p bin

point1_jar_name     := TestFairLock.jar
point1_jar_packages := point1
point1_jar_main_class := point1/FairLockTest

point20_jar_name := Point20.jar
point20_jar_packages := point1 \
			point2/point0
point20_jar_main_class := point2/point0/Testcase

point21_jar_name := Point21.jar
point21_jar_packages :=	point2/point1
point21_jar_main_class := point2/point1/Testcase

point31_jar_name := Point31.jar
point31_jar_packages :=	point3/point1
point31_jar_main_class := point3/point1/Testcase

$(eval $(call make-jar,point1_jar))
$(eval $(call make-jar,point20_jar))
$(eval $(call make-jar,point21_jar))
$(eval $(call make-jar,point31_jar))
# $(foreach j, $(jar_list), $(eval $(call make-jar,$j)))

.PHONY: clean
clean:
	@rm -rf $(OUTPUT_DIR)
	@rm -rf $(BIN_DIR)

.PHONY: classpath
classpath:
	@echo CLASSPATH='$(CLASSPATH)'

.PHONY: check-config
check-config:
	@echo Checking configuration...
	$(call check-file, $(class_path) JAVA_HOME)

.PHONY: print
print:
	$(foreach v, $(V), \
	$(warning $v = $($v)))

