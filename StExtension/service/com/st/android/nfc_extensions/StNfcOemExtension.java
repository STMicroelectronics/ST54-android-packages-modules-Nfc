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

// import android.nfc.Flags;
// import android.nfc.INfcAdapter;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.NfcOemExtension;
import android.nfc.NfcRoutingTableEntry;
import android.nfc.OemLogItems;
import android.nfc.RoutingTableAidEntry;
import android.nfc.RoutingTableTechnologyEntry;
import android.nfc.Tag;
import android.nfc.cardemulation.ApduServiceInfo;
import android.nfc.cardemulation.CardEmulation;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

// import java.util.logging.Handler;

public final class StNfcOemExtension {
    private static final String TAG = "StNfcOemExtension";
    private NfcAdapter mNfcAdapter = null;
    private int mLibStPropNciVersion = -1;
    ManufacturerData mManufacturerData = null;

    public StNfcOemExtension() {}

    private boolean StOemCheckStateAndLibVersion(int minversion) {
        /* As the version is reset to -1 when changing from STATE_ON, we just need to check the version here */
        return (mLibStPropNciVersion >= minversion);
    }

    private class StNfcOemExtensionCallback implements NfcOemExtension.Callback {

        StNfcOemExtensionCallback(/* Object o */ ) {}

        /**
         * Notify Oem to tag is connected or not ex - if tag is connected notify cover and Nfctest
         * app if app is in testing mode
         *
         * @param connected status of the tag true if tag is connected otherwise false
         */
        @Override
        public void onTagConnected(boolean connected) {
            Log.d(TAG, "StNfcOemExtensionCallback.onTagConnected");
        }

        /**
         * Update the Nfc Adapter State
         *
         * @param state new state that need to be updated
         */
        @Override
        public void onStateUpdated(int state) {
            String stateString = "";
            switch (state) {
                case NfcAdapter.STATE_OFF:
                    stateString = "STATE_OFF";
                    break;
                case NfcAdapter.STATE_TURNING_ON:
                    stateString = "STATE_TURNING_ON";
                    break;
                case NfcAdapter.STATE_ON:
                    stateString = "STATE_ON";
                    break;
                case NfcAdapter.STATE_TURNING_OFF:
                    stateString = "STATE_TURNING_OFF";
                    break;
            }
            Log.d(TAG, "StNfcOemExtensionCallback.onStateUpdated: " + stateString);
            if (state == NfcAdapter.STATE_ON) {
                mHandler.sendEmptyMessage(MSG_PROCESS_STATE_ON);
            } else if (state == NfcAdapter.STATE_TURNING_OFF) {
                mHandler.sendEmptyMessage(MSG_PROCESS_STATE_TURNING_OFF);
            }
        }

        /**
         * Check if NfcService apply routing method need to be skipped for some feature.
         *
         * @param isSkipped The {@link Consumer} to be completed. If apply routing can be skipped,
         *     the {@link Consumer#accept(Object)} should be called with {@link Boolean#TRUE},
         *     otherwise call with {@link Boolean#FALSE}.
         */
        @Override
        public void onApplyRouting(Consumer<Boolean> isSkipped) {
            Log.d(TAG, "StNfcOemExtensionCallback.onApplyRouting");
            isSkipped.accept(false);
        }

        /**
         * Check if NfcService ndefRead method need to be skipped To skip and start checking for
         * presence of tag
         *
         * @param isSkipped The {@link Consumer} to be completed. If Ndef read can be skipped, the
         *     {@link Consumer#accept(Object)} should be called with {@link Boolean#TRUE}, otherwise
         *     call with {@link Boolean#FALSE}.
         */
        @Override
        public void onNdefRead(Consumer<Boolean> isSkipped) {
            Log.d(TAG, "StNfcOemExtensionCallback.onNdefRead");
            isSkipped.accept(false);
        }

        /**
         * Method to check if Nfc is allowed to be enabled by OEMs.
         *
         * @param isAllowed The {@link Consumer} to be completed. If enabling NFC is allowed, the
         *     {@link Consumer#accept(Object)} should be called with {@link Boolean#TRUE}, otherwise
         *     call with {@link Boolean#FALSE}. false if NFC cannot be enabled at this time.
         */
        @Override
        public void onEnableRequested(Consumer<Boolean> isAllowed) {
            Log.d(TAG, "StNfcOemExtensionCallback.onEnableRequested");
            isAllowed.accept(true);
        }

        /**
         * Method to check if Nfc is allowed to be disabled by OEMs.
         *
         * @param isAllowed The {@link Consumer} to be completed. If disabling NFC is allowed, the
         *     {@link Consumer#accept(Object)} should be called with {@link Boolean#TRUE}, otherwise
         *     call with {@link Boolean#FALSE}. false if NFC cannot be disabled at this time.
         */
        @Override
        public void onDisableRequested(Consumer<Boolean> isAllowed) {
            Log.d(TAG, "StNfcOemExtensionCallback.onDisableRequested");
            isAllowed.accept(true);
        }

        /** Callback to indicate that Nfc starts to boot. */
        @Override
        public void onBootStarted() {
            Log.d(TAG, "StNfcOemExtensionCallback.onBootStarted");
        }

        /** Callback to indicate that Nfc starts to enable. */
        @Override
        public void onEnableStarted() {
            Log.d(TAG, "StNfcOemExtensionCallback.onEnableStarted");
        }

        /** Callback to indicate that Nfc starts to disable. */
        @Override
        public void onDisableStarted() {
            Log.d(TAG, "StNfcOemExtensionCallback.onDisableStarted");
        }

        /**
         * Callback to indicate if NFC boots successfully or not.
         *
         * @param status the status code indicating if boot finished successfully
         */
        @Override
        public void onBootFinished(int status) {
            Log.d(TAG, "StNfcOemExtensionCallback.onBootFinished");
        }

        /**
         * Callback to indicate if NFC is successfully enabled.
         *
         * @param status the status code indicating if enable finished successfully
         */
        @Override
        public void onEnableFinished(int status) {
            Log.d(TAG, "StNfcOemExtensionCallback.onEnableFinished: " + status);
        }

        /**
         * Callback to indicate if NFC is successfully disabled.
         *
         * @param status the status code indicating if disable finished successfully
         */
        @Override
        public void onDisableFinished(int status) {
            Log.d(TAG, "StNfcOemExtensionCallback.onDisableFinished");
        }

        /**
         * Check if NfcService tag dispatch need to be skipped.
         *
         * @param isSkipped The {@link Consumer} to be completed. If tag dispatch can be skipped,
         *     the {@link Consumer#accept(Object)} should be called with {@link Boolean#TRUE},
         *     otherwise call with {@link Boolean#FALSE}.
         */
        @Override
        public void onTagDispatch(Consumer<Boolean> isSkipped) {
            Log.d(TAG, "StNfcOemExtensionCallback.onTagDispatch");
            isSkipped.accept(false);
        }

        /**
         * Notifies routing configuration is changed.
         *
         * @param isCommitRoutingSkipped The {@link Consumer} to be completed. If routing commit
         *     should be skipped, the {@link Consumer#accept(Object)} should be called with {@link
         *     Boolean#TRUE}, otherwise call with {@link Boolean#FALSE}.
         */
        @Override
        public void onRoutingChanged(Consumer<Boolean> isCommitRoutingSkipped) {
            Log.d(TAG, "StNfcOemExtensionCallback.onRoutingChanged");
            isCommitRoutingSkipped.accept(false);
        }

        /**
         * API to activate start stop cpu boost on hce event.
         *
         * <p>When HCE is activated, transferring data, and deactivated, must call this method to
         * activate, start and stop cpu boost respectively.
         *
         * @param action Flag indicating actions to activate, start and stop cpu boost.
         */
        @Override
        public void onHceEventReceived(int action) {
            Log.d(TAG, "StNfcOemExtensionCallback.onHceEventReceived");
        }

        /**
         * API to notify when reader option has been changed using {@link
         * NfcAdapter#enableReaderOption(boolean)} by some app.
         *
         * @param enabled Flag indicating ReaderMode enabled/disabled
         */
        @Override
        public void onReaderOptionChanged(boolean enabled) {
            Log.d(TAG, "StNfcOemExtensionCallback.onReaderOptionChanged");
        }

        /**
         * Notifies NFC is activated in listen mode. NFC Forum NCI-2.3 ch.5.2.6 specification
         *
         * <p>NFCC is ready to communicate with a Card reader
         *
         * @param isActivated true, if card emulation activated, else de-activated.
         */
        @Override
        public void onCardEmulationActivated(boolean isActivated) {
            Log.d(TAG, "StNfcOemExtensionCallback.onCardEmulationActivated");
        }

        /**
         * Notifies the Remote NFC Endpoint RF Field is detected. NFC Forum NCI-2.3 ch.5.3
         * specification
         *
         * @param isActive true, if RF Field is ON, else RF Field is OFF.
         */
        @Override
        public void onRfFieldDetected(boolean isActive) {
            Log.d(TAG, "StNfcOemExtensionCallback.onRfFieldDetected");
        }

        /**
         * Notifies the NFC RF discovery is started or in the IDLE state. NFC Forum NCI-2.3 ch.5.2
         * specification
         *
         * @param isDiscoveryStarted true, if RF discovery started, else RF state is Idle.
         */
        @Override
        public void onRfDiscoveryStarted(boolean isDiscoveryStarted) {
            Log.d(
                    TAG,
                    "StNfcOemExtensionCallback.onRfDiscoveryStarted(" + isDiscoveryStarted + ")");

            if (isDiscoveryStarted) {
                if (mCardSwitchMonitorRunnable != null) {
                    Log.d(
                            TAG,
                            "StNfcOemExtensionCallback.onRfDiscoveryStarted: cleaning Card Switch"
                                    + " runnable and restart discovery");
                    mHandler.removeCallbacks(mCardSwitchMonitorRunnable);
                    mCardSwitchMonitorRunnable = null;
                    backFromIdle();
                }
            }
        }

        /**
         * Notifies the NFCEE (NFC Execution Environment) Listen has been activated.
         *
         * @param isActivated true, if EE Listen is ON, else EE Listen is OFF.
         */
        @Override
        public void onEeListenActivated(boolean isActivated) {
            Log.d(TAG, "StNfcOemExtensionCallback.onEeListenActivated");
            if (mIntfActivatedNtfCallback != null) {
                // TODO: return data from stpropnci lib if neeeded
                try {
                    mIntfActivatedNtfCallback.onIntfActivatedNtfReceived(null);
                } catch (RemoteException e) {
                    Log.e(TAG, "StNfcOemExtensionCallback.onEeListenActivated: e=" + e.toString());
                }
            }
        }

        /**
         * Notifies that some NFCEE (NFC Execution Environment) has been updated.
         *
         * <p>This indicates that some applet has been installed/updated/removed in one of the
         * NFCEE's.
         */
        @Override
        public void onEeUpdated() {
            Log.d(TAG, "StNfcOemExtensionCallback.onEeUpdated");
        }

        /**
         * Gets the intent to find the OEM package in the OEM App market. If the consumer returns
         * {@code null} or a timeout occurs, the intent from the first available package will be
         * used instead.
         *
         * @param packages the OEM packages name stored in the tag
         * @param intentConsumer The {@link Consumer} to be completed. The {@link
         *     Consumer#accept(Object)} should be called with the Intent required.
         */
        @Override
        public void onGetOemAppSearchIntent(
                List<String> packages, Consumer<Intent> intentConsumer) {
            Log.d(TAG, "StNfcOemExtensionCallback.onGetOemAppSearchIntent");
            intentConsumer.accept(null);
        }

        /**
         * Checks if the NDEF message contains any specific OEM package executable content
         *
         * @param tag the {@link android.nfc.Tag Tag}
         * @param message NDEF Message to read from tag
         * @param hasOemExecutableContent The {@link Consumer} to be completed. If there is OEM
         *     package executable content, the {@link Consumer#accept(Object)} should be called with
         *     {@link Boolean#TRUE}, otherwise call with {@link Boolean#FALSE}.
         */
        @Override
        public void onNdefMessage(
                Tag tag, NdefMessage message, Consumer<Boolean> hasOemExecutableContent) {
            Log.d(TAG, "StNfcOemExtensionCallback.onNdefMessage");
            hasOemExecutableContent.accept(false);
        }

        /**
         * Callback to indicate the app chooser activity should be launched for handling CE
         * transaction. This is invoked for example when there are more than 1 app installed that
         * can handle the HCE transaction. OEMs can launch the Activity based on their requirement.
         *
         * @param selectedAid the selected AID from APDU
         * @param services {@link ApduServiceInfo} of the service triggering the activity
         * @param failedComponent the component failed to be resolved
         * @param category the category of the service
         */
        @Override
        public void onLaunchHceAppChooserActivity(
                String selectedAid,
                List<ApduServiceInfo> services,
                ComponentName failedComponent,
                String category) {
            Log.d(TAG, "StNfcOemExtensionCallback.onLaunchHceAppChooserActivity");
        }

        /**
         * Callback to indicate tap again dialog should be launched for handling HCE transaction.
         * This is invoked for example when a CE service needs the device to unlocked before
         * handling the transaction. OEMs can launch the Activity based on their requirement.
         *
         * @param service {@link ApduServiceInfo} of the service triggering the dialog
         * @param category the category of the service
         */
        @Override
        public void onLaunchHceTapAgainDialog(ApduServiceInfo service, String category) {
            Log.d(TAG, "StNfcOemExtensionCallback.onLaunchHceTapAgainDialog");
        }

        /**
         * Callback to indicate that routing table is full and the OEM can optionally launch a
         * dialog to request the user to remove some Card Emulation apps from the device to free
         * routing table space.
         */
        @Override
        public void onRoutingTableFull() {
            Log.d(TAG, "StNfcOemExtensionCallback.onRoutingTableFull");
        }

        /**
         * Callback when OEM specified log event are notified.
         *
         * @param item the log items that contains log information of NFC event.
         */
        @Override
        public void onLogEventNotified(OemLogItems item) {
            Log.d(TAG, "StNfcOemExtensionCallback.onLogEventNotified");
        }

        /**
         * Callback to to extract OEM defined packages from given NDEF message when a NFC tag is
         * detected. These are used to handle NFC tags encoded with a proprietary format for storing
         * app name (Android native app format).
         *
         * @param message NDEF message containing OEM package names
         * @param packageConsumer The {@link Consumer} to be completed. The {@link
         *     Consumer#accept(Object)} should be called with the list of package names.
         */
        @Override
        public void onExtractOemPackages(
                NdefMessage message, Consumer<List<String>> packageConsumer) {
            Log.d(TAG, "StNfcOemExtensionCallback.onExtractOemPackages");
            packageConsumer.accept(null);
        }
    }

    /*
     * Usage for caller:
     * synchronized (mStNfcVendorNciCb.mSync) {
     * clearLastData();
     * mNfcAdapter.sendVendorNciMessage(...);
     * mStNfcVendorNciCb.mSync.wait(timeoutMS);
     * // check if lastRspPayload != null
     * optionally, wait again for ntf.
     * }
     */
    private class StNfcVendorNciCallback implements NfcAdapter.NfcVendorNciCallback {
        public Object mSync;
        public int expectedOid;

        public int lastRspGid;
        public int lastRspOid;
        public byte[] lastRspPayload;

        public boolean lastNtfOnly;
        public int lastNtfGid;
        public int lastNtfOid;
        public byte[] lastNtfPayload;

        StNfcVendorNciCallback(/* Object o1, Object o2 */ ) {
            mSync = new Object();
        }

        public void clearLastDataAndSetExpectedOID(int oid, boolean ntfOnly) {
            lastRspGid = -1;
            lastRspOid = -1;
            lastRspPayload = null;
            lastNtfGid = -1;
            lastNtfOid = -1;
            lastNtfPayload = null;
            expectedOid = oid;
            lastNtfOnly = ntfOnly;
        }

        @Override
        public void onVendorNciResponse(int gid, int oid, byte[] payload) {
            Log.d(
                    TAG,
                    "StNfcVendorNciCallback.onVendorNciResponse g:"
                            + Integer.toHexString(gid)
                            + ", o:"
                            + Integer.toHexString(oid)
                            + ", payload: "
                            + bytesToString(payload));
            synchronized (mSync) {
                lastRspGid = gid;
                lastRspOid = oid;
                lastRspPayload = payload;
                if (expectedOid < 0 || expectedOid == oid) {
                    mSync.notify();
                    expectedOid = -1;
                }
            }
        }

        @Override
        public void onVendorNciNotification(int gid, int oid, byte[] payload) {
            Log.d(
                    TAG,
                    "StNfcVendorNciCallback.onVendorNciNotification g:"
                            + Integer.toHexString(gid)
                            + ", o:"
                            + Integer.toHexString(oid)
                            + ", payload: "
                            + bytesToString(payload));
            synchronized (mSync) {
                if (expectedOid < 0 || expectedOid == oid) {
                    lastNtfPayload = payload;
                    lastNtfGid = (gid & 0xF);
                    lastNtfOid = oid;
                    if (lastNtfOnly) {
                        expectedOid = -1;
                        mSync.notify();
                    }
                }
            }
            if ((gid & 0xF) == ST_GID && oid == ST_OID) {
                onStNciNotification(payload);
            }
            if (mNtfCb != null) {
                // Bug in AOSP, need to mask gif of vendor NTF
                mNtfCb.onVendorNciNotification((gid & 0xF), oid, payload);
            }
        }
    }

    private NfcOemExtension mNfcOemExtension = null;
    private StNfcOemExtensionCallback mStNfcOemExtensionCb = null;
    private StNfcVendorNciCallback mStNfcVendorNciCb = null;
    private StControllerAlwaysOnListener mStControllerAlwaysOnListenerCb = null;
    private Context mContext = null;

    public interface StNfcOemExtensionVendorNtfCallback {
        public void onVendorNciNotification(int gid, int oid, byte[] payload);
    }

    private StNfcOemExtensionVendorNtfCallback mNtfCb;

    private boolean mHasSetAlwaysOn = false;

    private class StControllerAlwaysOnListener implements NfcAdapter.ControllerAlwaysOnListener {
        public Object mSync;
        public boolean mEnabled = false;
        public boolean mUpdated = false;

        StControllerAlwaysOnListener(/* Object o1, Object o2 */ ) {
            mSync = new Object();
        }

        @Override
        public void onControllerAlwaysOnChanged(boolean isEnabled) {
            synchronized (mSync) {
                Log.d(TAG, "onControllerAlwaysOnChanged: isEnabled=" + isEnabled);
                mEnabled = isEnabled;
                mUpdated = true;
                mSync.notify();
            }
        }
    }

    private void enterAlwaysOn() {
        if (!mHasSetAlwaysOn) {
            synchronized (mStControllerAlwaysOnListenerCb.mSync) {
                mStControllerAlwaysOnListenerCb.mUpdated = false;
                mNfcOemExtension.setControllerAlwaysOnMode(
                        NfcOemExtension.ENABLE_DEFAULT /* TRANSPARENT */);
                try {
                    mStControllerAlwaysOnListenerCb.mSync.wait(1000);
                } catch (InterruptedException e) {

                }
                if (!mStControllerAlwaysOnListenerCb.mUpdated
                        || !mStControllerAlwaysOnListenerCb.mEnabled) {
                    Log.e(TAG, "enterAlwaysOn: Failed to set alwayson mode in 1sec");
                }
            }
            mHasSetAlwaysOn = true;
        }
    }

    private void exitAlwaysOn() {
        if (mHasSetAlwaysOn) {
            // stop always on
            synchronized (mStControllerAlwaysOnListenerCb.mSync) {
                mStControllerAlwaysOnListenerCb.mUpdated = false;
                mNfcOemExtension.setControllerAlwaysOnMode(NfcOemExtension.DISABLE);
                try {
                    mStControllerAlwaysOnListenerCb.mSync.wait(1000);
                } catch (InterruptedException e) {

                }
                if (!mStControllerAlwaysOnListenerCb.mUpdated
                        || mStControllerAlwaysOnListenerCb.mEnabled) {
                    Log.e(TAG, "exitAlwaysOn: Failed to clear alwayson mode in 1sec");
                }
            }
        }
        mHasSetAlwaysOn = false;
    }

    private boolean goToIdle() {
        boolean result = true;

        // Are we in phone ON or OFF ?
        if (mNfcAdapter.isEnabled()) {
            // stop polling
            mNfcOemExtension.pausePolling(0); // 0 = pause indefinitely
            mHasSetAlwaysOn = false;
        } else {
            enterAlwaysOn();
        }

        return result;
    }

    private void backFromIdle() {
        // Now restore the state of NFC
        if (!mHasSetAlwaysOn) {
            // resume polling
            mNfcOemExtension.resumePolling();
        } else {
            exitAlwaysOn();
        }
    }

    private boolean mIsStarted = false;

    private void doRegister() {
        Log.d(TAG, "registerLower");
        mNfcAdapter = NfcAdapter.getDefaultAdapter(mContext);

        mNfcOemExtension = mNfcAdapter.getNfcOemExtension();
        mStNfcOemExtensionCb = new StNfcOemExtensionCallback();

        mStNfcVendorNciCb = new StNfcVendorNciCallback();

        mNfcOemExtension.registerCallback(
                Executors.newSingleThreadExecutor(), mStNfcOemExtensionCb);

        mNfcAdapter.registerNfcVendorNciCallback(
                Executors.newSingleThreadExecutor(), mStNfcVendorNciCb);

        mStControllerAlwaysOnListenerCb = new StControllerAlwaysOnListener();
        mNfcAdapter.registerControllerAlwaysOnListener(
                Executors.newSingleThreadExecutor(), mStControllerAlwaysOnListenerCb);
    }

    private void doUnregister() {
        Log.d(TAG, "doUnregister");
        // mNfcOemExtension.unregisterCallback(mStNfcOemExtensionCb);
        mNfcAdapter.unregisterNfcVendorNciCallback(mStNfcVendorNciCb);
        mNfcAdapter.unregisterControllerAlwaysOnListener(mStControllerAlwaysOnListenerCb);
    }

    public void register(Context c, StNfcOemExtensionVendorNtfCallback ntfCb) {
        Log.d(TAG, "register: (extensions version: DEV)");
        mContext = c;
        mNtfCb = ntfCb;
        doRegister();
        Log.d(TAG, "register: done");
    }

    public void unregister() {
        Log.d(TAG, "unregister");
        mNtfCb = null;
        doUnregister();
        Log.d(TAG, "unregister: done");
    }

    /* Called when the NFC service is sending OEM_EXTENSION_INIT */
    public void doTaskBoot() {
        Log.d(TAG, "doTaskBoot");
        if (mIsStarted && (mNtfCb != null)) {
            Log.d(TAG, "doTaskBoot: looks like NFC service restarted...");
            doRegister();
        }
        mNfcOemExtension.triggerInitialization();
        mIsStarted = true;
    }

    /**************** just utility functions *******************/

    public static String bytesToString(byte[] bytes) {
        if (bytes == null) return "";

        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02X", b & 0xFF));

        return sb.toString();
    }

    private Activity getActivity(Context context) {
        if (context == null) {
            return null;
        } else if (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            } else {
                return getActivity(((ContextWrapper) context).getBaseContext());
            }
        }

        return null;
    }

    /* Send a command following suboid template */
    private void sendVendorCommand(
            byte gid, byte oid, byte subOid, boolean isSubOid, byte[] payload) {
        int length = (isSubOid ? payload.length + 1 : payload.length);
        byte[] newpayload = new byte[length];
        if (isSubOid) {
            newpayload[0] = subOid;
            System.arraycopy(payload, 0, newpayload, 1, payload.length);
        } else {
            System.arraycopy(payload, 0, newpayload, 0, payload.length);
        }

        Log.d(TAG, "sendVendorCommand: " + bytesToString(newpayload));
        mNfcAdapter.sendVendorNciMessage(NfcAdapter.MESSAGE_TYPE_COMMAND, gid, oid, newpayload);
    }

    /* Exchange a vendor CMD, returns the RSP if success */
    private byte[] exchangeVendorCmdRsp(
            byte gid,
            byte oid,
            byte subOid,
            boolean isSubOid,
            byte[] payload,
            boolean rspWithoutOid) {
        byte[] rspPayload;
        byte[] ntfPayload;

        // send the command
        synchronized (mStNfcVendorNciCb.mSync) {
            mStNfcVendorNciCb.clearLastDataAndSetExpectedOID(oid, false);
            sendVendorCommand(gid, oid, subOid, true, payload);
            try {
                mStNfcVendorNciCb.mSync.wait(1000);
            } catch (InterruptedException e) {

            }
            rspPayload = mStNfcVendorNciCb.lastRspPayload;
            ntfPayload = mStNfcVendorNciCb.lastNtfPayload;
        }
        // Check response from the chip.
        if (rspPayload == null) {
            if (ntfPayload != null) {
                Log.e(TAG, "exchangeVendorCmdRsp: Received NTF instead of response");
            } else {
                Log.e(TAG, "exchangeVendorCmdRsp: No response received");
            }
            return null;
        }

        if (!rspWithoutOid) {
            if (rspPayload[0] != subOid) {
                Log.e(TAG, "exchangeVendorCmdRsp: Received response mismatch");
                return null;
            }
            if (rspPayload[1] != 0x00) {
                Log.e(TAG, "exchangeVendorCmdRsp: Status code in response is not SUCCESS");
            }
        }

        /* It was successful */
        return rspPayload;
    }

    /* NCI GID */
    private static final byte NCI_GID_CORE = 0x00;
    private static final byte NCI_GID_RF_MANAGE = 0x01;
    private static final byte NCI_GID_EE_MANAGE = 0x02;

    /* NCI CORE OID */
    private static final byte NCI_MSG_CORE_SET_CONFIG = 0x02;

    /**********************/
    /* ST proprietary NCI */
    /**********************/
    public static final byte ST_GID = (byte) 0xF;

    public static final byte ST_OID = (byte) 0x01;

    // SubGID / SubOID definitions :
    public static final byte ST_PROP_NCI_GET_STPROPNCI_VERSION_SUBOID = (byte) 0x01;
    public static final byte ST_PROP_NCI_GET_MANUF_DATA_SUBOID = (byte) 0x02;
    public static final byte ST_PROP_NCI_GET_NFCEE_ID_LIST = (byte) 0x03;
    public static final byte ST_PROP_NCI_SETUP_ADPU_GATE = (byte) 0x04;
    public static final byte ST_PROP_NCI_TRANSCEIVE_ADPU_GATE = (byte) 0x05;
    public static final byte ST_PROP_NCI_NFCEE_ACTION_NTF_AID_WITH_SW = (byte) 0x06;

    public static final byte NCI_ST_GET_SWP_STATUS = (byte) 0x20;
    public static final byte NCI_ST_GET_RESO_FREQ = (byte) 0x22;

    public static final byte NCI_ST_RESET_ESE = (byte) 0x80;

    /*************************/
    /* ST proprietary FW NCI */
    /*************************/
    public static final byte ST_NCI_MSG_PROP = (byte) 0x02;

    public static final byte ST_NCI_PROP_SET_CONFIG = (byte) 0x02;
    public static final byte ST_NCI_PROP_GET_CONFIG = (byte) 0x03;

    /* ST PROP NCI PARAM */
    public static final byte NCI_PARAM_ID_PROP_RF_SET_LISTEN_IOT_SEQ = (byte) 0xA4;
    public static final byte NCI_PARAM_ID_PROP_TEMPORARY_FORCED_SAK = (byte) 0xA5;

    private void sendStCommand(byte code, byte[] payload) {
        sendVendorCommand(ST_GID, ST_OID, code, true, payload);
    }

    private void onStNciNotification(byte[] payload) {
        /* this is called each time there is a notification received with ST OID */
        switch (payload[0]) {
            case ST_PROP_NCI_NFCEE_ACTION_NTF_AID_WITH_SW:
                {
                    byte nfcee_id = payload[1];
                    byte[] aid = Arrays.copyOfRange(payload, 6, 6 + payload[5]);
                    if (payload[5] != aid.length) {
                        Log.e(
                                TAG,
                                "onStNciNotification(ST_PROP_NCI_NFCEE_ACTION_NTF_AID_WITH_SW): "
                                        + " structure issue !");
                    }
                    byte[] sw = Arrays.copyOfRange(payload, payload.length - 2, payload.length);
                    Log.d(
                            TAG,
                            "onStNciNotification(ST_PROP_NCI_NFCEE_ACTION_NTF_AID_WITH_SW): Routed"
                                    + " to "
                                    + Integer.toHexString(nfcee_id & 0xFF)
                                    + ", AID="
                                    + bytesToString(aid)
                                    + ", SW="
                                    + bytesToString(sw));

                    if (mActionNtfCallback != null) {
                        try {
                            mActionNtfCallback.onNfceeActionNtfReceived(
                                    (int) (nfcee_id & 0xFF),
                                    Arrays.copyOfRange(payload, 2, payload.length));
                        } catch (RemoteException e) {
                            Log.e(
                                    TAG,
                                    "onStNciNotification(ST_PROP_NCI_NFCEE_ACTION_NTF_AID_WITH_SW):"
                                            + " e="
                                            + e.toString());
                        }
                    }
                }
                break;
        }
    }

    /****************** ST basic functions with stpropnci library **********************/

    /* Returns <0 in case of error  */
    public int testGetStPropNciVersion() {
        byte[] rspPayload =
                exchangeVendorCmdRsp(
                        ST_GID,
                        ST_OID,
                        ST_PROP_NCI_GET_STPROPNCI_VERSION_SUBOID,
                        true,
                        new byte[] {},
                        false);

        // Check response from the chip.
        if (rspPayload == null) {
            Log.e(TAG, "testGetStPropNciVersion: error, no rsp");
            return -1;
        }
        if (rspPayload[1] != 0x00) {
            Log.e(TAG, "testGetStPropNciVersion: failed RSP");
            return -1;
        }
        /* It was successful */
        return ((((int) rspPayload[2] & 0xFF) << 8) | ((int) rspPayload[3] & 0xFF)) & 0xFFFF;
    }

    /****************** Factory tests **********************/

    public boolean testGetSwpState(byte nfcee_id) {
        if (!StOemCheckStateAndLibVersion(1)) {
            Log.e(TAG, "testGetSwpState: NFC State or lower library not suitable for this command");
            return false;
        }

        byte[] rspPayload =
                exchangeVendorCmdRsp(
                        ST_GID, ST_OID, NCI_ST_GET_SWP_STATUS, true, new byte[] {nfcee_id}, false);
        // Check response from the chip.
        if (rspPayload == null) {
            Log.e(TAG, "testGetSwpState: error, no rsp");
            return false;
        }
        if (rspPayload[1] != 0x00) {
            Log.e(TAG, "testGetSwpState: failed RSP");
            return false;
        }
        if (rspPayload[2] != 0x00) {
            Log.e(TAG, "testGetSwpState: SWP status is not OK(active)");
            return false;
        }
        // It was successful
        return true;
    }

    public int testGetResoFreq() {
        byte[] rspPayload;
        int freq = -1;

        Log.d(TAG, "testGetResoFreq");
        boolean result = goToIdle();
        if (!result) {
            return -1;
        }

        rspPayload =
                exchangeVendorCmdRsp(
                        ST_GID, ST_OID, NCI_ST_GET_RESO_FREQ, true, new byte[] {}, false);

        // Check response from the chip.
        if (rspPayload == null) {
            Log.e(TAG, "testGetResoFreq: error, no rsp");
            return -1;
        }
        if (rspPayload[1] != 0x00) {
            Log.e(TAG, "testGetResoFreq: failed RSP");
            return -1;
        }
        freq = ((((int) rspPayload[2] & 0xFF) << 8) | ((int) rspPayload[3] & 0xFF)) & 0xFFFF;

        backFromIdle();
        Log.d(TAG, "testGetResoFreq(done)");

        // It was successful.
        return freq;
    }

    /****************** Reset SE **********************/

    public boolean testResetSe(byte trigger) {
        byte[] rspPayload;
        byte[] ntfPayload;

        Log.d(TAG, "testResetSe: trigger=" + String.format("%02x", trigger));

        synchronized (mStNfcVendorNciCb.mSync) {
            mStNfcVendorNciCb.clearLastDataAndSetExpectedOID(ST_OID, false);
            sendStCommand(NCI_ST_RESET_ESE, new byte[] {trigger});
            try {
                mStNfcVendorNciCb.mSync.wait(1000);
            } catch (InterruptedException e) {

            }
            rspPayload = mStNfcVendorNciCb.lastRspPayload;
            ntfPayload = mStNfcVendorNciCb.lastNtfPayload;
        }
        // Check response from the chip.
        if (rspPayload == null) {
            if (ntfPayload != null) {
                Log.e(TAG, "testResetSe: NTF instead of RSP to NCI_ST_RESET_ESE");
            } else {
                Log.e(TAG, "testResetSe: No RSP to NCI_ST_RESET_ESE");
            }
            return false;
        }

        return true;
    }

    /****************** Get pipe list **********************/

    public final class PipeInfo {
        int sourceHost;
        int sourceGate;
        int destHost;
        int destGate;
        int pipeId;
        int pipeState;

        public String display() {
            return "PipeInfo: sourceHost="
                    + String.format("%02x", this.sourceHost)
                    + ", sourceGate="
                    + String.format("%02x", this.sourceGate)
                    + ", destHost="
                    + String.format("%02x", destHost)
                    + ", destGate="
                    + String.format("%02x", destGate)
                    + ", pipeId="
                    + String.format("%02x", pipeId)
                    + ", pipeState="
                    + String.format("%02x", pipeState);
        }
    }

    Map<Byte, List<PipeInfo>> mPipesMap = new HashMap<>();
    List<Byte> mNfceeList = new ArrayList<>();
    int mNbNfcee = 0;

    void getPipeList() {
        Log.d(TAG, "getPipeList: get list of active NFCCE");
        byte[] rspPayload =
                exchangeVendorCmdRsp(
                        ST_GID, ST_OID, ST_PROP_NCI_GET_NFCEE_ID_LIST, true, new byte[] {}, false);

        // Check response from the chip.
        if (rspPayload == null) {
            Log.e(TAG, "getPipeList: error, no rsp");
            return;
        }
        if (rspPayload[1] != 0x00) {
            Log.e(TAG, "getPipeList: ST_PROP_NCI_GET_NFCEE_ID_LIST => failed RSP");
            return;
        }
        // get data
        mNbNfcee = rspPayload[2];
        Log.d(TAG, "getPipeList: nb active NFCEE=" + mNbNfcee);
        mPipesMap.clear();
        int attr = 0;

        for (int i = 1; i <= mNbNfcee; i++) {
            byte hostId = (rspPayload[2 + i]);
            Log.d(TAG, "getPipeList: NFCEE ID=" + String.format("%02x", hostId));
            mNfceeList.add(hostId);
            switch (hostId) {
                case (byte) 0x81:
                    attr = 8 | 2;
                    break;
                case (byte) 0x83:
                    attr = 8 | 3;
                    break;
                case (byte) 0x82: // eSE
                case (byte) 0x86: // eUICC-SE
                case (byte) 0x87:
                case (byte) 0x89:
                    attr = 8 | 3;
                    break;
                case (byte) 0x85:
                    attr = 8 | 4;
                    break;
                default:
                    continue;
            }
            byte[] getPipeListData = {(byte) attr, (byte) 0x82, 0x1, 0x1};

            Log.d(TAG, "getPipeList: get list of pipes for active NFCEEs");
            byte[] rspPipeList =
                    exchangeVendorCmdRsp(
                            ST_GID,
                            ST_NCI_MSG_PROP,
                            ST_NCI_PROP_GET_CONFIG,
                            true,
                            getPipeListData,
                            true);

            // Check response from the chip.
            if (rspPipeList == null) {
                Log.e(TAG, "getPipeList: an error happened");
                return;
            }
            if (rspPayload[1] != 0x00) {
                Log.e(TAG, "getPipeList: ST_NCI_PROP_GET_CONFIG => failed RSP");
                return;
            }
            int idx = 0;
            int nb_entry = (rspPipeList[3] & 0xFF) / 12;
            Log.d(TAG, "getPipeList: nb entries=" + nb_entry);
            List<PipeInfo> pipesList = new ArrayList<>();
            while (idx < nb_entry) {
                if (rspPipeList[12 * idx + 4] != 0) {
                    PipeInfo pipeInfo = new PipeInfo();
                    pipeInfo.sourceHost = rspPipeList[12 * idx + 4] & 0xFF;
                    pipeInfo.sourceGate = rspPipeList[12 * idx + 5] & 0xFF;
                    pipeInfo.destHost = rspPipeList[12 * idx + 6] & 0xFF;
                    pipeInfo.destGate = rspPipeList[12 * idx + 7] & 0xFF;
                    pipeInfo.pipeId = rspPipeList[12 * idx + 8] & 0xFF;
                    pipeInfo.pipeState = rspPipeList[12 * idx + 9] & 0xFF;
                    pipesList.add(pipeInfo);
                }
                idx++;
            }
            mPipesMap.put(hostId, pipesList);
        }

        for (var entry : mPipesMap.entrySet()) {
            Log.d(TAG, "getPipeList: route=" + String.format("%02x", entry.getKey()));
            for (var list : entry.getValue()) {
                Log.d(TAG, "getPipeList: " + list.display());
            }
        }
    }

    /****************** Setup APDU gate **********************/

    int mTxWaitingTime;

    boolean setupApduGate() {
        Log.d(TAG, "setupApduGate");
        byte[] rspPayload =
                exchangeVendorCmdRsp(
                        ST_GID, ST_OID, ST_PROP_NCI_SETUP_ADPU_GATE, true, new byte[] {}, false);

        // Check response from the chip.
        if (rspPayload == null) {
            Log.e(TAG, "setupApduGate: error, no rsp");
            return false;
        }
        if (rspPayload[1] != 0x00) {
            Log.e(TAG, "setupApduGate: ST_PROP_NCI_SETUP_ADPU_GATE => failed RSP");
            return false;
        }
        mTxWaitingTime = ((rspPayload[2] & 0xFF) << 8) | (rspPayload[3] & 0xFF);

        enterAlwaysOn();

        return true;
    }

    /****************** Transceive APDU gate **********************/

    byte[] transceiveOnApduGate(byte[] cmd) {
        Log.d(TAG, "transceiveOnApduGate: cmd=" + bytesToString(cmd));
        byte[] rspPayload;
        byte[] ntfPayload;

        // send the command
        synchronized (mStNfcVendorNciCb.mSync) {
            mStNfcVendorNciCb.clearLastDataAndSetExpectedOID(ST_OID, false);
            sendVendorCommand(ST_GID, ST_OID, ST_PROP_NCI_TRANSCEIVE_ADPU_GATE, true, cmd);
            try {
                mStNfcVendorNciCb.mSync.wait(1000);
            } catch (InterruptedException e) {

            }
            rspPayload = mStNfcVendorNciCb.lastRspPayload;
            ntfPayload = mStNfcVendorNciCb.lastNtfPayload;

            // Check response from the chip.
            if (rspPayload == null) {
                if (ntfPayload != null) {
                    Log.e(TAG, "transceiveOnApduGate: Received NTF instead of response");
                } else {
                    Log.e(TAG, "transceiveOnApduGate: No response received");
                }
                return null;
            }

            if (rspPayload[0] != ST_PROP_NCI_TRANSCEIVE_ADPU_GATE) {
                Log.e(TAG, "transceiveOnApduGate: Received response mismatch");
                return null;
            }
            if (rspPayload[1] != 0x00) {
                Log.e(TAG, "transceiveOnApduGate: Status code in response is not SUCCESS");
                return null;
            }

            while (mStNfcVendorNciCb.lastNtfPayload == null) {
                mStNfcVendorNciCb.clearLastDataAndSetExpectedOID(ST_OID, true);
                try {
                    mStNfcVendorNciCb.mSync.wait(mTxWaitingTime);
                } catch (InterruptedException e) {

                }
                if (mStNfcVendorNciCb.lastNtfOid == -1) {
                    Log.e(TAG, "transceiveOnApduGate: timeout");
                    return null;
                }
                ntfPayload = mStNfcVendorNciCb.lastNtfPayload;
            }
        }
        Log.d(TAG, "transceiveOnApduGate: rsp=" + bytesToString(ntfPayload));
        /* It was successful */
        return Arrays.copyOfRange(ntfPayload, 2, ntfPayload.length);
    }

    /****************** Close APDU gate **********************/

    void closeApduGate() {
        Log.d(TAG, "closeApduGate");

        exitAlwaysOn();
    }

    /****************** Call overwriteRoutingTable() **********************/

    void overwriteRoutingTable(int routeProto, int routeTechno, int routeAid, int routeSc) {
        Log.d(
                TAG,
                "overwriteRoutingTable: routeProto="
                        + routeProto
                        + ", routeTechno="
                        + routeTechno
                        + ", routeAid="
                        + routeAid
                        + ", routeSc="
                        + routeSc);
        mNfcOemExtension.overwriteRoutingTable(routeProto, routeTechno, routeAid, routeSc);
    }

    /****************** Call getRoutingTable() **********************/

    String convertTypeToString(int type) {
        switch (type) {
            case NfcRoutingTableEntry.TYPE_TECHNOLOGY:
                return NfcSettingsAdapterImpl.DEFAULT_AB_TECH_ROUTE;
            case NfcRoutingTableEntry.TYPE_PROTOCOL:
                return NfcSettingsAdapterImpl.DEFAULT_ISO_DEP_ROUTE;
            case NfcRoutingTableEntry.TYPE_AID:
                return NfcSettingsAdapterImpl.DEFAULT_AID_ROUTE;
            case NfcRoutingTableEntry.TYPE_SYSTEM_CODE:
                return NfcSettingsAdapterImpl.DEFAULT_SC_ROUTE;
            default:
                return "";
        }
    }

    String convertRouteTypeToString(int routeType) {
        switch (routeType) {
            case CardEmulation.PROTOCOL_AND_TECHNOLOGY_ROUTE_DH:
                return NfcSettingsAdapterImpl.HCE_ROUTE;
            case CardEmulation.PROTOCOL_AND_TECHNOLOGY_ROUTE_ESE:
                return NfcSettingsAdapterImpl.ESE_ROUTE;
            case CardEmulation.PROTOCOL_AND_TECHNOLOGY_ROUTE_UICC:
                return NfcSettingsAdapterImpl.UICC_ROUTE;
            case CardEmulation.PROTOCOL_AND_TECHNOLOGY_ROUTE_DEFAULT:
                return NfcSettingsAdapterImpl.DEFAULT_ROUTE;
            default:
                return "";
        }
    }

    List<DefaultRouteEntry> getRoutingTable() {
        Log.d(TAG, "getRoutingTable");
        List<DefaultRouteEntry> returnList = new ArrayList<>();
        List<NfcRoutingTableEntry> routeList = mNfcOemExtension.getRoutingTable();
        int typeBitmap = 0x00;
        for (NfcRoutingTableEntry entry : routeList) {
            String type = "";
            String route = convertRouteTypeToString(entry.getRouteType());
            switch (entry.getType()) {
                case NfcRoutingTableEntry.TYPE_TECHNOLOGY:
                    RoutingTableTechnologyEntry techEntry = (RoutingTableTechnologyEntry) entry;
                    if ((techEntry.getTechnology() == RoutingTableTechnologyEntry.TECHNOLOGY_A)
                            || (techEntry.getTechnology()
                                    == RoutingTableTechnologyEntry.TECHNOLOGY_B)) {
                        type = NfcSettingsAdapterImpl.DEFAULT_AB_TECH_ROUTE;
                    }
                    break;
                case NfcRoutingTableEntry.TYPE_PROTOCOL:
                    type = NfcSettingsAdapterImpl.DEFAULT_ISO_DEP_ROUTE;
                    break;
                case NfcRoutingTableEntry.TYPE_AID:
                    RoutingTableAidEntry aidEntry = (RoutingTableAidEntry) entry;
                    if (aidEntry.getAid().contains("Empty_AID")) {
                        type = NfcSettingsAdapterImpl.DEFAULT_AID_ROUTE;
                    }
                    break;
                case NfcRoutingTableEntry.TYPE_SYSTEM_CODE:
                    type = NfcSettingsAdapterImpl.DEFAULT_SC_ROUTE;
                    break;
                default:
                    break;
            }
            if (!type.isEmpty()) {
                // If this type is not yet stored
                if ((typeBitmap & (0x01 << entry.getType())) == 0x00) {
                    typeBitmap |= (0x01 << entry.getType());
                    DefaultRouteEntry defaultEntry = new DefaultRouteEntry(type, route);
                    returnList.add(defaultEntry);
                }
            }
        }
        Log.d(TAG, "getRoutingTable: returnList=" + returnList.toString());
        return returnList;
    }

    /****************** Call getRoutingTable() **********************/

    public ManufacturerData getManufacturerData() {
        if (mManufacturerData == null) {
            byte[] rspPayload =
                    exchangeVendorCmdRsp(
                            ST_GID,
                            ST_OID,
                            ST_PROP_NCI_GET_MANUF_DATA_SUBOID,
                            true,
                            new byte[] {},
                            false);

            // Check response from the chip.
            if (rspPayload == null) {
                Log.e(TAG, "getManufacturerData: error, no rsp");
                return null;
            }
            if (rspPayload[1] != 0x00) {
                Log.e(TAG, "getManufacturerData: ST_PROP_NCI_SETUP_ADPU_GATE => failed RSP");
                return null;
            }
            byte[] manuData = Arrays.copyOfRange(rspPayload, 2, rspPayload.length);
            mManufacturerData = new ManufacturerData(manuData);
        }
        Log.d(TAG, "getManufacturerData: chip version=" + mManufacturerData.getChipName());
        return mManufacturerData;
    }

    /****************** Call setForceSak() **********************/
    public boolean setForceSAK(boolean enabled, int sak) {
        Log.d(TAG, "setForceSAK");
        boolean status = false;
        goToIdle();
        byte eseId = (byte) 0xFF;
        for (byte nfceeId : mNfceeList) {
            if ((nfceeId == (byte) 0x82) || (nfceeId == (byte) 0x86)) {
                eseId = nfceeId;
                break;
            }
        }
        byte sakVal = (enabled ? (byte) sak : 0x00);
        if (eseId != (byte) 0xFF) {
            byte[] payload = {0x01, NCI_PARAM_ID_PROP_TEMPORARY_FORCED_SAK, 0x02, eseId, sakVal};
            byte[] rsp =
                    exchangeVendorCmdRsp(
                            NCI_GID_CORE,
                            NCI_MSG_CORE_SET_CONFIG,
                            (byte) 0x00,
                            false,
                            payload,
                            false);
            if (rsp != null) {
                status = (rsp[0] == 0x00);
            }
        }
        backFromIdle();
        return status;
    }

    /****************** Call sendPropSetConfig() **********************/
    private void applyPropRfConfig() {
        byte[] propApplyRfConfig = {0x0A};
        exchangeVendorCmdRsp(
                NCI_GID_CORE,
                NCI_MSG_CORE_SET_CONFIG,
                (byte) 0x00,
                false,
                propApplyRfConfig,
                false);
    }

    public void sendPropSetConfig(int configSubSetId, int paramId, byte[] param) {
        Log.d(TAG, "sendPropSetConfig: configSubSetId=" + configSubSetId + ", paramId=" + paramId);
        goToIdle();
        byte[] payloadStart = {
            0x04, 0x00, (byte) configSubSetId, 0x01, (byte) paramId, (byte) param.length
        };
        byte[] payload = Arrays.copyOf(payloadStart, payloadStart.length + param.length);
        System.arraycopy(param, 0, payload, payloadStart.length, param.length);
        exchangeVendorCmdRsp(
                NCI_GID_CORE, NCI_MSG_CORE_SET_CONFIG, (byte) 0x00, false, payload, false);

        if ((configSubSetId == 0x10) || (configSubSetId == 0x17)) {
            applyPropRfConfig();
        }
        backFromIdle();
    }

    /****************** Call sendPropSetConfigs() **********************/
    public void sendPropSetConfigs(int[] configSubSetIds, int[] paramIds, List<ByteArray> params) {
        Log.d(
                TAG,
                "sendPropSetConfigs: nb config="
                        + configSubSetIds.length
                        + ", nb paramIds="
                        + paramIds.length);
        goToIdle();
        for (int i = 0; i < configSubSetIds.length; i++) {
            byte[] payloadStart = {
                0x04,
                0x00,
                (byte) configSubSetIds[i],
                0x01,
                (byte) paramIds[i],
                (byte) params.get(i).getByteArray().length
            };
            byte[] payload =
                    Arrays.copyOf(
                            payloadStart,
                            payloadStart.length + params.get(i).getByteArray().length);
            System.arraycopy(
                    params.get(i).getByteArray(),
                    0,
                    payload,
                    payloadStart.length,
                    params.get(i).getByteArray().length);
            exchangeVendorCmdRsp(
                    NCI_GID_CORE, NCI_MSG_CORE_SET_CONFIG, (byte) 0x00, false, payload, false);
            if ((configSubSetIds[i] == 0x10) || (configSubSetIds[i] == 0x17)) {
                applyPropRfConfig();
            }
        }
        backFromIdle();
    }

    /****************** Call getFirmwareVersion() **********************/
    public byte[] getFirmwareVersion() {
        byte[] info = mManufacturerData.getFwVersion();
        String fwVersion =
                String.format("%02X", info[0] & 0xFF)
                        + "."
                        + String.format("%02X", info[1] & 0xFF)
                        + "."
                        + String.format("%02X", info[2] & 0xFF)
                        + String.format("%02X", info[3] & 0xFF);
        Log.d(TAG, "getFirmwareVersion: " + fwVersion);
        return info;
    }

    /****************** Call getHWVersion() **********************/
    public byte[] getHWVersion() {
        return mManufacturerData.getHwVersion();
    }

    /****************** Call registerNfceeActionNtfCallback() **********************/
    INfceeActionNtfCallback mActionNtfCallback;

    public boolean registerNfceeActionNtfCallback(INfceeActionNtfCallback cb) {
        Log.i(TAG, "registerNfceeActionNtfCallback");
        mActionNtfCallback = cb;
        return true;
    }

    /****************** Call unregisterNfceeActionNtfCallback() **********************/
    public boolean unregisterNfceeActionNtfCallback() {
        Log.i(TAG, "unregisterNfceeActionNtfCallback");
        mActionNtfCallback = null;
        return false;
    }

    /****************** Call registerIntfActivatedNtfCallback() **********************/
    private IIntfActivatedNtfCallback mIntfActivatedNtfCallback;

    public boolean registerIntfActivatedNtfCallback(IIntfActivatedNtfCallback cb) {
        Log.i(TAG, "registerIntfActivatedNtfCallback");
        mIntfActivatedNtfCallback = cb;
        return true;
    }

    /****************** Call unregisterIntfActivatedNtfCallback() **********************/
    public boolean unregisterIntfActivatedNtfCallback() {
        Log.i(TAG, "unregisterIntfActivatedNtfCallback");
        mIntfActivatedNtfCallback = null;
        return true;
    }

    /****************** Call seteSEInCardSwitchingExt() **********************/
    CardSwitchMonitorRunnable mCardSwitchMonitorRunnable = null;

    public boolean seteSEInCardSwitchingExt(boolean inswitching, int nbOp) {
        Log.i(TAG, "seteSEInCardSwitchingExt");
        goToIdle();
        // Start timer of 500ms. Stop it onRfDiscoverySTarted().
        // If it timeouts, restart discovery
        if (mCardSwitchMonitorRunnable == null) {
            mCardSwitchMonitorRunnable = new CardSwitchMonitorRunnable();
            mHandler.postDelayed(mCardSwitchMonitorRunnable, 1000);
        }
        return true;
    }

    class CardSwitchMonitorRunnable implements Runnable {

        @Override
        public void run() {
            synchronized (this) {
                Log.e(TAG, "mCardSwitchMonitorRunnable.run: timer elapsed, restart discovery");
                backFromIdle();
                mCardSwitchMonitorRunnable = null;
            }
        }
    }

    /*********************************************************/
    /****************** Message Handler **********************/
    /*********************************************************/

    static final int MSG_PROCESS_STATE_ON = 1;

    static final int MSG_PROCESS_STATE_TURNING_OFF = 2;
    // static final int MSG_NEXT_STEP = 2;
    // static final int MSG_RETRY = 3;

    final Handler mHandler =
            new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case MSG_PROCESS_STATE_ON:
                            Log.d(TAG, "mHandler(MSG_PROCESS_STATE_ON)");
                            mLibStPropNciVersion = testGetStPropNciVersion();
                            if (mLibStPropNciVersion < 0) {
                                Log.e(TAG, "mHandler: failed to read" + " libstpropnci version");
                            } else {
                                Log.d(
                                        TAG,
                                        "mHandler: libstpropnci version=" + mLibStPropNciVersion);
                                // Retrieve pipe list for actives HCI NFCEE
                                getPipeList();
                                getManufacturerData();
                                getFirmwareVersion();
                            }
                            break;
                        case MSG_PROCESS_STATE_TURNING_OFF:
                            Log.d(TAG, "mHandler(MSG_PROCESS_STATE_TURNING_OFF)");
                            mManufacturerData = null;
                            /* In other states, don't let 3rd party call extensions */
                            mLibStPropNciVersion = -1;
                            break;
                        default:
                            break;
                    }
                }
            };
}
