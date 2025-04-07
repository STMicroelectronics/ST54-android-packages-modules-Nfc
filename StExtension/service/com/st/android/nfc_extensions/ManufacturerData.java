package com.st.android.nfc_extensions;

import java.util.Arrays;

public class ManufacturerData {
    private static final String TAG = "NfcManufacturerData";
    byte[] mData;

    public ManufacturerData(byte[] data) {
        mData = data;
    }

    public String getChipName() {
        HwInfo info = new HwInfo(mData);
        return info.getChipId();
    }

    public byte[] getCustomerData() {
        return Arrays.copyOfRange(mData, 21, mData.length - 1);
    }

    public byte[] getFwVersion() {
        // 4 bytes for FW version info
        return Arrays.copyOfRange(mData, 2, 6);
    }

    public byte[] getHwVersion() {
        // 2 bytes for FW version info
        return Arrays.copyOfRange(mData, 0, 2);
    }
}
