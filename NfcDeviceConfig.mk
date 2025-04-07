# Device configuration file to be included from device.mk file, e.g.
#
#   -include vendor/st/nfc/st21nfc/NfcDeviceConfig.mk

######################################################################
##########################  SYSTEM image  ############################
######################################################################

################################################
## Configuration for ST NFC packages
PRODUCT_PACKAGES += \
    libstnfc_nci_jni \
    Nfc_st \
    StNfcExtensionService \
    com.st.android.nfc_extensions \
    com.st.android.nfc_extensions.xml \
    libnfc_vendor_extn_st \

PRODUCT_COPY_FILES += \
   frameworks/native/data/etc/com.nxp.mifare.xml:$(TARGET_COPY_OUT_SYSTEM_EXT)/etc/permissions/com.nxp.mifare.xml:st \

################################################
## NFC Forum testing support (Analog / Digital / TagOp / LLCP/SNEP)
## The following package can be safely removed if you don t plan to use DTA:
PRODUCT_PACKAGES += \
   StDta

# ################################################
# ## Factory tests support
# PRODUCT_PACKAGES += \
#    libstfactory \
#    stfactorydemo \
