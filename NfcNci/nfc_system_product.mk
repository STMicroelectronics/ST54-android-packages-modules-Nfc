ifeq ($(TARGET_FWK_SUPPORTS_FULL_VALUEADDS), true)
TARGET_USES_QSSI_STM_NFC := true

STM_SYSTEM_NFC := NfcNci
STM_SYSTEM_NFC += Tag
STM_SYSTEM_NFC += libnfc_vendor_extn_st
STM_SYSTEM_NFC += libstnfc_nci_jni
STM_SYSTEM_NFC += com.st.android.nfc_extensions
STM_SYSTEM_NFC += com.st.android.nfc_extensions.xml
STM_SYSTEM_NFC += com.android.nfc_extras

ifeq ($(strip $(TARGET_USES_QSSI_STM_NFC)),true)
PRODUCT_COPY_FILES += \
    vendor/st/opensource/commonsys/packages/modules/Nfc/libnfc-nci-conf/libnfc-nci.conf:$(TARGET_COPY_OUT_PRODUCT)/etc/libnfc-nci.conf
PRODUCT_PACKAGES += $(STM_SYSTEM_NFC)
endif
endif
