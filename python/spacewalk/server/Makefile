# Makefile for spacewalk backend
#

# what is the backend top dir
TOP     = ..

# Specific stuff
SUBDIR		= server

FILES		= __init__
SPACEWALK_FILES		= __init__ \
		  apacheServer apacheHandler apacheAuth apacheRequest \
                  apacheUploadServer \
		  rhnAuthPAM rhnCapability rhnChannel \
                  rhnDependency \
                  rhnAction \
                  rhnHandler rhnImport rhnMapping rhnLib rhnPackage \
                  rhnPackageUpload basePackageUpload rhnRepository \
                  rhnSession rhnUser taskomatic \
                  suseEula db_config
SUBDIRS		= rhnSQL rhnServer handlers importlib repomd

include $(TOP)/Makefile.defs
