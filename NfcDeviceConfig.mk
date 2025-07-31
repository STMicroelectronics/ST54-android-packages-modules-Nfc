# Device configuration file to be included from device.mk file, e.g.
#
#   -include vendor/st/nfc/st21nfc/NfcDeviceConfig.mk

######################################################################
##########################  SYSTEM image  ############################
######################################################################

################################################
# ST NFC - mandatory packages to fully support NFC chipsets
PRODUCT_PACKAGES += \
    libnfc_vendor_extn_st \

# Configure ST nfc_vendor_extn for legacy devices upgrading (older HAL)
PRODUCT_SYSTEM_PROPERTIES += \
   persist.nfc_vendor_extn.lib_file_name=libnfc_vendor_extn_st.so \

# Feature supported by ST and not part of default AOSP
PRODUCT_COPY_FILES += \
   frameworks/native/data/etc/com.nxp.mifare.xml:$(TARGET_COPY_OUT_SYSTEM)/etc/permissions/com.nxp.mifare.xml:st \

# copy libnfc-nci.conf to product/etc/ to take priority over default config
ifneq ($(strip $(TARGET_BUILD_VARIANT)),user)
   PRODUCT_COPY_FILES += \
      vendor/st/opensource/commonsys/packages/modules/Nfc/libnfc-nci-conf/libnfc-nci.conf:$(TARGET_COPY_OUT_PRODUCT)/etc/libnfc-nci.conf:st
else
   PRODUCT_COPY_FILES += \
      vendor/st/opensource/commonsys/packages/modules/Nfc/libnfc-nci-conf/libnfc-nci.conf.user:$(TARGET_COPY_OUT_PRODUCT)/etc/libnfc-nci.conf:st
endif

################################################
# ST NFC - optional packages to have access to additional features (extensions)
PRODUCT_PACKAGES += \
    com.st.android.nfc_extensions \
    NfcOverlaySt \

################################################
## NFC Forum testing support (Analog / Digital / TagOp)
## The following package can be safely removed if you don t plan to use DTA:
ifneq ($(strip $(TARGET_BUILD_VARIANT)),user)
   PRODUCT_PACKAGES += \
      StDta
endif
