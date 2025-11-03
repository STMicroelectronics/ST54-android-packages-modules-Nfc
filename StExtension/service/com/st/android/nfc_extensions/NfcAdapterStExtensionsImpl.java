/*
 * Copyright (C) 2024 ST Microelectronics S.A.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.st.android.nfc_extensions;

import android.os.IBinder;
import android.util.Log;

import com.st.android.nfc_extensions.StNfcOemExtension.PipeInfo;

import java.util.List;
import java.util.Map;

public class NfcAdapterStExtensionsImpl extends INfcAdapterStExtensions.Stub {
    private static final String TAG = "NfcAdapterStExtensionsImpl";

    private final StNfcOemExtension mStNfcOemExtension;

    private final INfcWalletAdapter.Stub mWalletBinder;
    private NfcWalletAdapterImpl mWalletImpl;
    private final INfcSettingsAdapter.Stub mSettingsBinder;

    private NfcAdapterStExtensionsImpl(StNfcOemExtension e) {
        mStNfcOemExtension = e;
        mWalletImpl = new NfcWalletAdapterImpl(mStNfcOemExtension);
        mWalletBinder = mWalletImpl;
        mSettingsBinder = new NfcSettingsAdapterImpl(mStNfcOemExtension);
    }

    private static NfcAdapterStExtensionsImpl INSTANCE;

    public static NfcAdapterStExtensionsImpl getInstance(StNfcOemExtension e) {
        if (INSTANCE == null) {
            INSTANCE = new NfcAdapterStExtensionsImpl(e);
        }
        return INSTANCE;
    }

    public NfcWalletAdapterImpl getWalletImpl() {
        return mWalletImpl;
    }

    public INfcSettingsAdapter getSettingsBinder() {
        return mSettingsBinder;
    }

    @Override
    public IBinder asBinder() {
        return this;
    }

    @Override
    public byte[] getFirmwareVersion() {
        return mStNfcOemExtension.getFirmwareVersion();
    }

    @Override
    public byte[] getHWVersion() {
        return mStNfcOemExtension.getHWVersion();
    }

    @Override
    @Deprecated
    public int loopback() {
        Log.w(TAG, "loopback: Deprecated API, nothing done");
        return 0;
    }

    @Override
    @Deprecated
    public boolean getHceCapability() {
        Log.w(TAG, "getHceCapability: Deprecated API, nothing done");
        return true;
    }

    @Override
    @Deprecated
    public void setRfConfiguration(int modeBitmap, byte[] techArray) {
        Log.w(TAG, "setRfConfiguration: Deprecated API, nothing done");
    }

    @Override
    @Deprecated
    public int getRfConfiguration(byte[] techArray) {
        Log.w(TAG, "getRfConfiguration: Deprecated API, nothing done");
        return 0;
    }

    @Override
    @Deprecated
    public void setRfBitmap(int modeBitmap) {
        Log.w(TAG, "setRfBitmap: Deprecated API, nothing done");
    }

    @Override
    public boolean getProprietaryConfigSettings(int SubSetID, int byteNb, int bitNb) {
        // TODO
        Log.e(TAG, "getProprietaryConfigSettings() not supported yet");

        return false;
    }

    @Override
    public void setProprietaryConfigSettings(int SubSetID, int byteNb, int bitNb, boolean status) {
        // TODO
        Log.e(TAG, "setProprietaryConfigSettings() not supported yet");
    }

    @Override
    public int getPipesList(int hostId, byte[] list) {
        Map<Byte, List<PipeInfo>> pipesMap = mStNfcOemExtension.retrievePipesList();
        for (var entry : pipesMap.entrySet()) {
            if (hostId == (entry.getKey() & 0xFF)) {
                List<PipeInfo> pipesInfo = entry.getValue();
                int idx = 0;
                for (var pipeInfo : pipesInfo) {
                    list[idx] = (byte) pipeInfo.pipeId;
                    idx++;
                }
                return pipesInfo.size();
            }
        }
        return 0;
    }

    @Override
    public void getPipeInfo(int hostId, int pipeId, byte[] info) {
        Map<Byte, List<PipeInfo>> pipesMap = mStNfcOemExtension.retrievePipesList();
        for (var entry : pipesMap.entrySet()) {
            if (hostId == (entry.getKey() & 0xFF)) {
                List<PipeInfo> pipesInfo = entry.getValue();
                int idx = 0;
                for (var pipeInfo : pipesInfo) {
                    if (pipeInfo.pipeId == pipeId) {
                        info[0] = (byte) pipeInfo.pipeState;
                        info[1] = (byte) pipeInfo.sourceHost;
                        info[2] = (byte) pipeInfo.sourceGate;
                        info[3] = (byte) pipeInfo.destHost;
                        break;
                    }
                }
            }
        }
    }

    @Override
    @Deprecated
    public byte[] getATR() {
        Log.w(TAG, "getATR: Deprecated API, nothing done");
        return null;
    }

    @Deprecated
    public boolean connectEE(int ceeId) {
        Log.i(TAG, "connectEE() - Deprecated, nothing done");
        return false;
    }

    @Deprecated
    public byte[] transceiveEE(int cee_id, byte[] dataCmd) {
        Log.i(TAG, "transceiveEE() - Deprecated, nothing done");
        return null;
    }

    @Deprecated
    public boolean disconnectEE(int cee_id) {
        Log.i(TAG, "disconnectEE: Deprecated, nothing done");
        return false;
    }

    @Deprecated
    public int connectGate(int host_id, int gate_id) {
        Log.w(TAG, "connectGate: Deprecated API, nothing done");
        return 0;
    }

    @Deprecated
    public byte[] transceive(int pipe_id, int hciCmd, byte[] dataIn) {
        Log.w(TAG, "transceive: Deprecated API, nothing done");
        return null;
    }

    @Deprecated
    public void disconnectGate(int pipe_id) {
        Log.w(TAG, "disconnectGate: Deprecated API, nothing done");
    }

    @Deprecated
    public void setNciConfig(int param_id, byte[] param) {
        Log.w(TAG, "setNciConfig: Deprecated API, nothing done");
    }

    @Deprecated
    public byte[] getNciConfig(int param_id) {
        Log.w(TAG, "getNciConfig: Deprecated API, nothing done");
        return null;
    }

    @Deprecated
    public int getAvailableHciHostList(byte[] nfceeId, byte[] conInfo) {
        // TODO
        Log.e(TAG, "getAvailableHciHostList: Deprecated API, nothing done");
        return -1;
    }

    @Deprecated
    public boolean getBitPropConfig(int configId, int byteNb, int bitNb) {
        Log.w(TAG, "getBitPropConfig: Deprecated API, nothing done");
        return false;
    }

    @Deprecated
    public void setBitPropConfig(int configId, int byteNb, int bitNb, boolean status) {
        Log.w(TAG, "setBitPropConfig: Deprecated API, nothing done");
    }

    public void forceRouting(int nfceeId, int power) {
        // TODO
        Log.e(TAG, "forceRouting() not supported yet");
    }

    public void stopforceRouting() {
        // TODO
        Log.e(TAG, "stopforceRouting() not supported yet");
    }

    public void sendPropSetConfig(int configSubSetId, int paramId, byte[] param) {
        Log.d(TAG, "sendPropSetConfig: configSubSetId=" + configSubSetId + ", paramId=" + paramId);
        mStNfcOemExtension.sendPropSetConfig(configSubSetId, paramId, param);
    }

    public void sendPropSetConfigs(int[] configSubSetIds, int[] paramIds, List<ByteArray> params) {
        Log.d(
                TAG,
                "sendPropSetConfigs: nb config="
                        + configSubSetIds.length
                        + ", nb paramIds="
                        + paramIds.length);
        mStNfcOemExtension.sendPropSetConfigs(configSubSetIds, paramIds, params);
    }

    public byte[] sendPropGetConfig(int configSubSetId, int paramId) {
        // TODO
        Log.e(TAG, "sendPropGetConfig() not supported yet");
        return null;
    }

    public byte[] sendPropTestCmd(int subCode, byte[] paramTx) {
        // TODO
        Log.e(TAG, "sendPropTestCmd() not supported yet");
        return null;
    }

    public byte[] getCustomerData() {
        Log.e(TAG, "getCustomerData");
        ManufacturerData manuData = mStNfcOemExtension.getManufacturerData();
        if (manuData == null) {
            return null;
        } else {
            return manuData.getCustomerData();
        }
    }

    @Override
    public INfcWalletAdapter getNfcWalletAdapterInterface() {
        return mWalletBinder;
    }

    @Override
    public INfcSettingsAdapter getNfcSettingsAdapterInterface() {
        return mSettingsBinder;
    }

    public void programHceParameters(
            boolean setConfig,
            byte bitFrameSdd,
            byte platformConfig,
            byte selInfo,
            byte[] nfcid1,
            byte rats,
            byte[] histBytes) {
        mStNfcOemExtension.programHceParameters(
                setConfig, bitFrameSdd, platformConfig, selInfo, nfcid1, rats, histBytes);
    }

    @Override
    public void seteSeReaderMode(boolean start) {
        // TODO
        Log.e(TAG, "seteSeReaderMode() not supported yet");
    }

    @Override
    @Deprecated
    public void registerNfcStackRestartCb(INfcStExtensionsRestartCb cb) {
        Log.e(TAG, "registerNfcStackRestartCb() is deprecated");
    }

    @Override
    public void unregisterNfcStackRestartCb() {
        Log.e(TAG, "unregisterNfcStackRestartCb() is deprecated");
    }

    @Override
    @Deprecated
    public INfcNdefNfceeAdapter getNfcNdefNfceeAdapterInterface() {
        Log.e(TAG, "getNfcNdefNfceeAdapterInterface() is deprecated");
        return null;
    }

    @Override
    public byte[] getNfceeIdList() {
        return mStNfcOemExtension.getNfceeList();
    }

    @Override
    public SdkVersion getServiceSdkVersion() {
        return new SdkVersion();
    }

    @Override
    public byte[] sendVendorNciMessage(byte[] cmd, int discFlags) {
        return mStNfcOemExtension.sendVendorNciMessage(cmd, discFlags);
    }

    @Override
    public void setTagDetectorStatus(boolean status) {
        mStNfcOemExtension.setTagDetectorStatus(status);
    }

    @Override
    public boolean getTagDetectorStatus() {
        return mStNfcOemExtension.getTagDetectorStatus();
    }
}
