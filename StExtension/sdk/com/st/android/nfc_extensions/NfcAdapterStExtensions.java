/*
 * Copyright (C) 2013 ST Microelectronics S.A.
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
 *
 * Provide extensions for the ST implementation of the NFC stack
 */
package com.st.android.nfc_extensions;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** This class contains a set of APIs that extend basic AOSP NFC support. */
public final class NfcAdapterStExtensions {

    private static final String TAG = "NfcAdapterStExtensions";

    /* Connection to StNfcExtensionService */
    private INfcAdapterStExtensions mNfcAdapterStExtInterface = null;
    private Context mContext = null;

    public interface NfcAdapterStExtensionsServiceConnection {
        public void onServiceConnected();

        public void onServiceDisconnected();
    }

    private NfcAdapterStExtensionsServiceConnection mConnectionCb;

    /**
     * Constructor for the {@link NfcAdapterStExtensions}
     *
     * @return
     */
    public NfcAdapterStExtensions(Context c, NfcAdapterStExtensionsServiceConnection cb) {
        mContext = c;
        mConnectionCb = cb;
    }

    public INfcAdapterStExtensions getNfcAdapterStExtensionsInterface() {
        return mNfcAdapterStExtInterface;
    }

    public void connectToService() {
        ServiceConnection connection =
                new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName name, IBinder service) {
                        Log.i(TAG, "onServiceConnected() - component: " + name.flattenToString());
                        mNfcAdapterStExtInterface =
                                INfcAdapterStExtensions.Stub.asInterface(service);
                        mConnectionCb.onServiceConnected();
                    }

                    @Override
                    public void onServiceDisconnected(ComponentName name) {
                        Log.i(
                                TAG,
                                "onServiceDisconnected() - component: " + name.flattenToString());
                        mNfcAdapterStExtInterface = null;
                        mConnectionCb.onServiceDisconnected();
                    }
                };

        // The value returned by bindSearch() only indicates whether binding was
        // successfully
        // initiated, based on preliminary checks like whether the service exists and
        // whether the
        // caller has the necessary permissions to bind. Binding is not complete until
        // ServiceConnection#onServiceConnected() is called.
        //
        // Binding is asynchronous. However, the process that calls onServiceConnected()
        // gets queued
        // and is likely to (eventually) be run by the same thread that called
        // bindService().
        // Because of that, it is not possible to block the thread that calls
        // bindService() until
        // onServiceConnected() is called.
        boolean bindingRequestedSuccessfully =
                mContext.bindService(
                        new Intent("com.st.android.nfc_extensions.StNfcExtensionService.BIND")
                                .setPackage("com.st.android.nfc_extensions"),
                        connection,
                        Context.BIND_AUTO_CREATE);

        Log.i(
                TAG,
                "connectToService() - NfcAdapterStExtensions(sdk version"
                        + " 25Q2-BP2A-20250405-Gen-25W14p0) binding requested:"
                        + bindingRequestedSuccessfully);
    }

    /**
     * Get the firmware version of the ST21NFCD chip
     *
     * @return An object of the type {@link FwVersion} that contains information about the FW
     *     version.
     */
    public FwVersion getFirmwareVersion() throws RemoteException {
        if (mNfcAdapterStExtInterface == null) {
            throw new RemoteException("Disconnected from service");
        }
        byte[] result = null;
        result = mNfcAdapterStExtInterface.getFirmwareVersion();

        FwVersion fwVersion = new FwVersion(result);
        return fwVersion;
    }

    /**
     * Get the HW version of the ST21NFCD chip
     *
     * @return An object of the type {@link HwInfo}.
     *     <p>This object contains information about the ST21NFCB HW version.
     */
    public HwInfo getHWVersion() throws RemoteException {
        if (mNfcAdapterStExtInterface == null) {
            throw new RemoteException("Disconnected from service");
        }
        byte[] result = null;
        result = mNfcAdapterStExtInterface.getHWVersion();

        HwInfo hwInfo = new HwInfo(result);
        Log.d(TAG, "getHWVersion: " + hwInfo.getChipId());
        return hwInfo;
    }

    /**
     * This API sets the current Tag Detector status in the CLF configuration parameters.
     *
     * <p>
     *
     * @param status True if the Tag Detector shall be enabled and false otherwise.
     */
    @Deprecated
    public void setTagDetectorStatus(boolean status) {
        Log.i(TAG, "setTagDetectorStatus()");

        // do we need this ? please contact ST if you use it.
        Log.e(TAG, "not supported yet");
    }

    /**
     * This API retrieves the current Tag detector status from the CLF configuration parameters.
     *
     * <p>
     *
     * @return true if the tag detector is enabled and false otherwise.
     */
    @Deprecated
    public boolean getTagDetectorStatus() {
        boolean status = false;
        Log.i(TAG, "getTagDetectorStatus()");

        // do we need this ? please contact ST if you use it.
        Log.e(TAG, "not supported yet");

        return (status);
    }

    /**
     * This API retrieves the list and status of pipes for a given host.
     *
     * <p>
     *
     * @param hostId Identity of the host to investigate
     * @return a structure {@link PipesInfo} containing the list and status of the pipes attached to
     *     the host.
     */
    public PipesInfo getPipesInfo(int hostId) throws RemoteException {
        if (mNfcAdapterStExtInterface == null) {
            throw new RemoteException("Disconnected from service");
        }
        Log.i(TAG, "getPipesInfo() - for host " + hostId);
        int nbPipes = 0;
        byte[] list = new byte[10];
        byte[] info = new byte[5];

        nbPipes = mNfcAdapterStExtInterface.getPipesList(hostId, list);
        Log.i(TAG, "getPipesInfo() - Found " + nbPipes + " for host " + hostId);
        PipesInfo retrievedInfo = new PipesInfo(nbPipes);

        for (int i = 0; i < nbPipes; i++) {
            Log.i(TAG, "getPipesInfo() - retrieving info for pipe " + list[i]);
            mNfcAdapterStExtInterface.getPipeInfo(hostId, list[i], info);
            retrievedInfo.setPipeInfo(list[i], info);
        }

        return retrievedInfo;
    }

    /**
     * This API retrieves the ATR of the eSE.
     *
     * <p>
     *
     * @return an array of bytes containing the ATR.
     */
    @Deprecated
    public byte[] getATR() {

        // DEPRECATED
        Log.e(TAG, "getATR() is not supported anymore");

        return null;
    }

    public static final String HCI_HOST_UICC1 = "SIM1";
    public static final String HCI_HOST_UICC2 = "SIM2";
    public static final String HCI_HOST_ESE = "ESE";
    public static final String HCI_HOST_EUICCSE = "eUICC-SE";
    public static final String HCI_HOST_DHSE = "DHSE";
    public static final String HCI_HOST_ACTIVE = "ACTIVE";
    public static final String HCI_HOST_INACTIVE = "INACTIVE";
    public static final String HCI_HOST_UNRESPONSIVE = "UNRESPONSIVE";

    public static final int NFA_EE_MAX_EE_SUPPORTED = 5;

    public Map<String, String> getAvailableHciHostList() throws RemoteException {
        if (mNfcAdapterStExtInterface == null) {
            throw new RemoteException("Disconnected from service");
        }
        Map<String, String> result = new HashMap<String, String>();
        byte[] nfceeId = new byte[NFA_EE_MAX_EE_SUPPORTED];
        byte[] conInfo = new byte[NFA_EE_MAX_EE_SUPPORTED];
        int nbHost = 0;
        int i;

        Log.i(TAG, "getAvailableHciHostList()");

        nbHost = mNfcAdapterStExtInterface.getAvailableHciHostList(nfceeId, conInfo);

        for (i = 0; i < nbHost; i++) {
            Log.i(
                    TAG,
                    "getHostList() - nfceeId["
                            + i
                            + "] = "
                            + nfceeId[i]
                            + ", conInfo["
                            + i
                            + "] = "
                            + conInfo[i]);
        }

        String nfcee;
        String status;

        for (i = 0; i < nbHost; i++) {
            nfcee = "";
            status = "";

            switch (nfceeId[i]) {
                case (byte) 0x81:
                    nfcee = HCI_HOST_UICC1;
                    break;

                case (byte) 0x82:
                    nfcee = HCI_HOST_ESE;
                    break;

                case (byte) 0x83:
                case (byte) 0x85:
                    nfcee = HCI_HOST_UICC2;
                    break;

                case (byte) 0x84:
                    nfcee = HCI_HOST_DHSE;
                    break;
                case (byte) 0x86:
                    nfcee = HCI_HOST_EUICCSE;
                    break;
            }

            switch (conInfo[i]) {
                case 0x00: // Active
                    status = HCI_HOST_ACTIVE;
                    break;
                case 0x01: // Inactive
                    status = HCI_HOST_INACTIVE;
                    break;
                case 0x02: // Unresponsive
                    status = HCI_HOST_UNRESPONSIVE;
                    break;
            }

            result.put(nfcee, status);
        }

        return result;
    }

    /**
     * This API gets status of the 2-UICC mode.
     *
     * <p>
     *
     * <p>
     *
     * @return status True if the dual uicc mode is enabled and false otherwise.
     */
    @Deprecated
    public boolean getDualSimFeature() {
        boolean status = false;

        // DEPRECATED
        Log.e(TAG, "getDualSimFeature() is not supported anymore");

        return status;
    }

    /**
     * This API sets the NFCC in 2 UICC mode.
     *
     * <p>
     *
     * <p>
     *
     * @param status True if the dual uicc mode shall be enabled and false otherwise.
     */
    @Deprecated
    public void setDualSimFeature(boolean status) {
        Log.i(TAG, "setDualSimFeature(" + status + ")");

        // DEPRECATED
        Log.e(TAG, "setDualSimFeature() is not supported anymore");
    }

    /**
     * This API allows to force all the routings to a given NFCEE id
     *
     * <p>
     */
    public void forceRouting(int nfceeId, int PowerState) throws RemoteException {
        if (mNfcAdapterStExtInterface == null) {
            throw new RemoteException("Disconnected from service");
        }
        mNfcAdapterStExtInterface.forceRouting(nfceeId, 0);
    }

    /**
     * This API allows to stop forcing all the routings to a given NFCEE id. The routing set by
     * SET_LISTEN_MODE_ROUTING is being applied.
     *
     * <p>
     */
    public void stopforceRouting() throws RemoteException {
        if (mNfcAdapterStExtInterface == null) {
            throw new RemoteException("Disconnected from service");
        }
        mNfcAdapterStExtInterface.stopforceRouting();
    }

    @Deprecated
    public byte getNfceeHwConfig() {
        byte conf = 0;

        Log.i(TAG, "getNfceeHwConfig()");

        // is this needed ?
        Log.e(TAG, "not supported yet");

        return conf;
    }

    @Deprecated
    public void setNfceeHwConfig(byte conf) {
        Log.i(TAG, "setNfceeHwConfig(" + conf + ")");

        // is this needed ?
        Log.e(TAG, "not supported yet");
    }

    /**
     * This API allows to set Nci params
     *
     * <p>
     */
    @Deprecated
    public void setNciConfig(int paramId, byte[] param) {
        Log.i(TAG, "setNciParam(" + paramId + ")");

        // is this needed ?
        Log.e(TAG, "not supported yet");
    }

    /**
     * This API gets the value of a NCI parameter.
     *
     * <p>
     *
     * <p>
     *
     * @param paramId NCI parameter id.
     * @return param NCI parameter value.
     */
    @Deprecated
    public byte[] getNciConfig(int paramId) {
        Log.i(TAG, "getNciParam(" + paramId + ")");

        // is this needed ?
        Log.e(TAG, "not supported yet");

        return null;
    }

    /**
     * This API sets a ST proprietary configuration in the NFCC.
     *
     * <p>
     *
     * <p>
     *
     * @param configSubSetId The identifier of the configuration sub-set.
     * @param paramId The identifier of the specific configuration parameter
     * @param param The value of the specific configuration parameter
     */
    public void sendPropSetConfig(int configSubSetId, int paramId, byte[] param)
            throws RemoteException {
        if (mNfcAdapterStExtInterface == null) {
            throw new RemoteException("Disconnected from service");
        }
        Log.i(TAG, "sendPropSetConfig(" + configSubSetId + ")");

        mNfcAdapterStExtInterface.sendPropSetConfig(configSubSetId, paramId, param);
    }

    /**
     * This API sets several ST proprietary configurations in the NFCC.
     *
     * <p>
     *
     * <p>
     *
     * @param configSubSetIds Array of identifier of the configuration sub-set.
     * @param paramIds Array of identifier of the specific configuration parameter
     * @param params list of byte[] values of the specific configuration parameter
     */
    public void sendPropSetConfig(
            List<Integer> configSubSetIds, List<Integer> paramIds, List<byte[]> params)
            throws RemoteException {
        if (mNfcAdapterStExtInterface == null) {
            throw new RemoteException("Disconnected from service");
        }
        Log.i(TAG, "sendPropSetConfig(" + configSubSetIds.size() + " values)");

        int[] subsets = new int[configSubSetIds.size()];
        int[] ids = new int[paramIds.size()];
        List<ByteArray> ba = new ArrayList<ByteArray>();

        /* Convert */
        for (int i = 0; i < configSubSetIds.size(); i++) {
            subsets[i] = configSubSetIds.get(i);
        }
        for (int i = 0; i < paramIds.size(); i++) {
            ids[i] = paramIds.get(i);
        }
        for (int i = 0; i < params.size(); i++) {
            ba.add(new ByteArray(params.get(i)));
        }

        /* Send to service */
        mNfcAdapterStExtInterface.sendPropSetConfigs(subsets, ids, ba);
    }

    /**
     * This API gets a ST proprietary configuration in the NFCC.
     *
     * <p>
     *
     * <p>
     *
     * @param configSubSetId The identifier of the configuration sub-set.
     * @param paramId The identifier of the specific configuration parameter
     * @param param The value of the specific configuration parameter
     */
    public byte[] sendPropGetConfig(int configSubSetId, int paramId) throws RemoteException {
        if (mNfcAdapterStExtInterface == null) {
            throw new RemoteException("Disconnected from service");
        }
        Log.i(TAG, "sendPropGetConfig(" + configSubSetId + ")");

        return mNfcAdapterStExtInterface.sendPropGetConfig(configSubSetId, paramId);
    }

    /**
     * This API allows to send Proprietary test commands.
     *
     * <p>
     */
    public byte[] sendPropTestCmd(int subCode, byte[] paramTx) throws RemoteException {
        if (mNfcAdapterStExtInterface == null) {
            throw new RemoteException("Disconnected from service");
        }
        Log.i(TAG, "sendPropTestCmd(" + subCode + ")");

        return mNfcAdapterStExtInterface.sendPropTestCmd(subCode, paramTx);
    }

    public byte[] getCustomerData() throws RemoteException {
        if (mNfcAdapterStExtInterface == null) {
            throw new RemoteException("Disconnected from service");
        }
        Log.i(TAG, "getCustomerData()");
        return mNfcAdapterStExtInterface.getCustomerData();
    }

    /**
     * This API sets the low power mode in the UICC configuration register.
     *
     * <p>
     *
     * <p>
     *
     * @param status True if the Low power shall be enabled and false otherwise.
     */
    @Deprecated
    public void setUiccLowPowerStatus(boolean status) {
        Log.i(TAG, "setUiccLowPowerStatus()");

        // is this needed ?
        Log.e(TAG, "not supported yet");
    }

    /**
     * This API retrieves the current low power mode in the UICC configuration register.
     *
     * <p>
     *
     * @return true if the Low power is enabled and false otherwise.
     */
    @Deprecated
    public boolean getUiccLowPowerStatus() {
        Log.i(TAG, "getUiccLowPowerStatus()");

        boolean status = false;

        // is this needed ?
        Log.e(TAG, "not supported yet");

        return (status);
    }

    public INfcWalletAdapter getNfcWalletAdapterInterface() throws RemoteException {
        if (mNfcAdapterStExtInterface == null) {
            throw new RemoteException("Disconnected from service");
        }
        Log.i(TAG, "getNfcWalletAdapterInterface()");
        return mNfcAdapterStExtInterface.getNfcWalletAdapterInterface();
    }

    /**
     * This API sets proprietary HCE parameters or restores the default configuration
     *
     * <p>
     *
     * @param setConfig True if proprietary HCE parameters to be set False if default HCE paramaters
     *     must be restored
     * @param bitFrameSdd LA_BIT_FRAME_SDD NCI parameter
     * @param platformConfig LA_PLATFORM_CONFIG NCI parameter
     * @param selInfo LA_SEL_INFO NCI parameter
     * @param nfcid1 LA_NCFID1 NCI parameter
     * @param rats LI_A_RATS_TB1 NCI parameter
     * @param histBytes LI_A_HIST_BY NCI parameter
     */
    public void programHceParameters(
            boolean setConfig,
            byte bitFrameSdd,
            byte platformConfig,
            byte selInfo,
            byte[] nfcid1,
            byte rats,
            byte[] histBytes)
            throws RemoteException {
        if (mNfcAdapterStExtInterface == null) {
            throw new RemoteException("Disconnected from service");
        }
        Log.i(TAG, "programHceParameters()");
        mNfcAdapterStExtInterface.programHceParameters(
                setConfig, bitFrameSdd, platformConfig, selInfo, nfcid1, rats, histBytes);
    }

    public INfcSettingsAdapter getNfcSettingsAdapterInterface() throws RemoteException {
        if (mNfcAdapterStExtInterface == null) {
            throw new RemoteException("Disconnected from service");
        }
        Log.i(TAG, "getNfcSettingsAdapterInterface()");

        return mNfcAdapterStExtInterface.getNfcSettingsAdapterInterface();
    }

    public void seteSeReaderMode(boolean start) throws RemoteException {
        if (mNfcAdapterStExtInterface == null) {
            throw new RemoteException("Disconnected from service");
        }
        Log.i(TAG, "seteSeReaderMode(" + start + ")");
        mNfcAdapterStExtInterface.seteSeReaderMode(start);
    }

    @Deprecated
    public void registerNfcStackRestartCb(INfcStExtensionsRestartCb cb) {
        Log.i(TAG, "registerNfcStackRestartCb()");

        // is this needed ?
        Log.e(TAG, "not supported yet");
    }

    @Deprecated
    public void unregisterNfcStackRestartCb() {
        Log.i(TAG, "unregisterNfcStackRestartCb()");

        // is this needed ?
        Log.e(TAG, "not supported yet");
    }

    @Deprecated
    public INfcNdefNfceeAdapter getNfcNdefNfceeAdapterInterface() {
        Log.i(TAG, "getNfcNdefNfceeAdapterInterface()");

        // DEPRECATED
        Log.e(TAG, "not supported anymore, use AOSP framework methods to interact with NDEF-NFCEE");

        return null;
    }
}
