ifeq ($(TARGET_FWK_SUPPORTS_FULL_VALUEADDS), true)
TARGET_USES_QSSI_STM_NFC := true

ifeq ($(strip $(TARGET_USES_QSSI_STM_NFC)),true)
$(call inherit-product, vendor/st/opensource/commonsys/packages/modules/Nfc/NfcDeviceConfig.mk)
endif
endif
