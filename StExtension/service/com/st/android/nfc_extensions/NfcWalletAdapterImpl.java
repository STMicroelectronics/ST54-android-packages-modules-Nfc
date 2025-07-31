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

public class NfcWalletAdapterImpl extends INfcWalletAdapter.Stub {
    private static final String TAG = "NfcWalletAdapterImpl";

    private final StNfcOemExtension mStNfcOemExtension;

    public NfcWalletAdapterImpl(StNfcOemExtension e) {
        mStNfcOemExtension = e;
    }

    @Override
    public IBinder asBinder() {
        return this;
    }

    // private IIntfActivatedNtfCallback mIntfActivatedNtfCallback;
    // private INfcWalletRawCallback mRawCallback;
    // private int mRawDuration;
    // private INfcWalletCeApduCallback mCeApduCallback;

    public boolean keepEseSwpActive(boolean enable) {
        // TODO
        Log.i(TAG, "keepEseSwpActive(" + enable + ") is not supported yet");
        return false;
    }

    public boolean setMuteTech(boolean muteA, boolean muteB, boolean muteF) {
        // TODO
        Log.i(TAG, "setMuteTech(" + muteA + ", " + muteB + ", " + muteF + ") is not supported yet");
        return false;
    }

    public boolean setObserverMode(boolean enable) {
        // TODO
        Log.i(TAG, "setObserverMode(" + enable + ")");
        return false;
    }

    @Deprecated
    public boolean registerStLogCallback(INfcWalletLogCallback cb) {
        // DEPRECATED
        Log.i(TAG, "registerStLogCallback() ==> deprecated");
        return false;
    }

    @Deprecated
    public boolean unregisterStLogCallback() {
        // DEPRECATED
        Log.i(TAG, "unregisterStLogCallback() ==> deprecated");
        return false;
    }

    @Deprecated
    public boolean rotateRfParameters(boolean reset) {
        // DEPRECATED
        Log.i(TAG, "rotateRfParameters(" + reset + ") ==> deprecated");
        return false;
    }

    public boolean setSEFelicaCardEnabled(boolean status) {
        // TODO
        Log.i(TAG, "setSEFelicaCardEnabled(" + status + ")");
        return false;
    }

    public boolean registerNfceeActionNtfCallback(INfceeActionNtfCallback cb) {
        Log.i(TAG, "registerNfceeActionNtfCallback");
        mStNfcOemExtension.registerNfceeActionNtfCallback(cb);
        return true;
    }

    public boolean unregisterNfceeActionNtfCallback() {
        Log.i(TAG, "unregisterNfceeActionNtfCallback");
        mStNfcOemExtension.unregisterNfceeActionNtfCallback();
        return true;
    }

    public boolean registerIntfActivatedNtfCallback(IIntfActivatedNtfCallback cb) {
        Log.i(TAG, "registerIntfActivatedNtfCallback");
        mStNfcOemExtension.registerIntfActivatedNtfCallback(cb);
        return true;
    }

    public boolean unregisterIntfActivatedNtfCallback() {
        Log.i(TAG, "unregisterIntfActivatedNtfCallback");
        mStNfcOemExtension.unregisterIntfActivatedNtfCallback();
        return true;
    }

    public boolean setForceSAK(boolean enabled, int sak) {
        Log.i(TAG, "setForceSAK");
        return mStNfcOemExtension.setForceSAK(enabled, sak);
    }

    public boolean seteSEInCardSwitching(boolean inswitching) {
        return seteSEInCardSwitchingExt(inswitching, 0);
    }

    public boolean seteSEInCardSwitchingExt(boolean inswitching, int nbOp) {
        Log.i(TAG, "seteSEInCardSwitchingExt");
        mStNfcOemExtension.seteSEInCardSwitchingExt(inswitching, nbOp);
        return true;
    }

    /** *************************** RAW mode for ISO14443-3 *************************** */
    // private boolean mRawIsAuth;

    // private boolean mRawIsEnabled;
    // private int mRawSEHandle = -1;
    // private byte mRawLogicalChannelNbr = 0;
    // private final ScheduledExecutorService mRawDeauthScheduler =
    // Executors.newScheduledThreadPool(1);
    // private ScheduledFuture<?> mRawDeauthScheduledTask = null;

    // private class RawDeauthRunnable implements Runnable {
    // public RawDeauthRunnable() {}

    // @Override
    // public void run() {
    // Log.d(TAG, "RAW auth expired");
    // rawRfAuth(0);
    // }
    // }

    public boolean registerRawRfAuthCallback(INfcWalletRawCallback cb) {
        // TODO
        Log.i(TAG, "registerRawRfAuthCallback()");
        return false;
        // mRawCallback = cb;
    }

    public boolean unregisterRawRfAuthCallback() {
        // TODO
        Log.i(TAG, "unregisterRawRfAuthCallback()");
        return false;
        // mRawCallback = null;
    }

    // // for debug
    // private boolean mUseLogicalChannel = true;

    /*
     * Open logical channel with eSE, send command to CLF to start monitoring.
     * if auth is successful (INfcWalletRawCallback), the auth will remain valid
     * for duration. Logical channel will be closed automatically when callback
     * is posted. This API can only be called when the foreground application
     * started reader mode A only already and when an INfcWalletRawCallback is
     * already registered.
     * Phases:
     * Initial: mRawIsAuth false, mRawDuration 0
     * PendingAuth: mRawIsAuth false, mRawDuration > 0
     * Authenticated: mRawIsAuth true, mRawDuration > 0
     */
    public boolean rawRfAuth(int duration) {
        // TODO
        Log.i(TAG, "rawRfAuth()");
        return false;
    }

    /* Exchange data with the eSE. First command shall be SELECT ISD. */
    public byte[] rawSeAuth(byte[] data) {
        // TODO
        Log.i(TAG, "rawSeAuth()");
        return null;
    }

    /* When RAW mode is authorized, use this APIs to start or stop it */
    public boolean rawRfMode(boolean enable) {
        // TODO
        Log.i(TAG, "rawRfMode()");
        return false;
    }

    /* RAW card access from JNI directly */
    public byte[] rawJniSeq(int i, byte[] ba) {
        // TODO
        Log.i(TAG, "rawJniSeq()");
        return null;
    }

    @Deprecated
    public byte[] getLogBuffer() {
        Log.e(TAG, "getLogBuffer() is deprecated");
        return null;
    }

    @Deprecated
    public boolean registerPollingLoopCallback(INfcWalletPollingLoopCallback cb) {
        Log.e(TAG, "registerPollingLoopCallback() is deprecated");
        return false;
    }

    @Deprecated
    public boolean unregisterPollingLoopCallback() {
        Log.e(TAG, "unregisterPollingLoopCallback() is deprecated");
        return false;
    }

    public boolean registerCeApduCallback(INfcWalletCeApduCallback cb) {
        // TODO
        Log.i(TAG, "registerCeApduCallback()");
        return false;
        // mCeApduCallback = cb;
    }

    // public void onCeApduData(byte[] data) {

    // final Runnable walletCb = new WalletCbRunnable(CB_CE_APDU, (Object)data,
    // null);
    // mWalletCbScheduledTask =
    // mWalletCbScheduler.schedule(walletCb, 0, TimeUnit.MILLISECONDS);
    // }

    public boolean unregisterCeApduCallback() {
        // TODO
        Log.i(TAG, "unregisterCeApduCallback()");
        return false;
        // mCeApduCallback = null;
    }

    // public boolean openApduGate() {
    //     Log.i(TAG, "openApduGate");
    //     return mStNfcOemExtension.setupApduGate();
    // }

    // public byte[] transceiveApduGate(byte[] data) {
    //     Log.i(TAG, "unregisterCeApduCallback");
    //     return mStNfcOemExtension.transceiveOnApduGate(data);
    // }

    // public void closeApduGate() {
    //     Log.i(TAG, "unregisterCeApduCallback");
    //     mStNfcOemExtension.closeApduGate();
    // }

    public boolean setRfCustomPollingFrames(byte[] rf_frames) {
        return mStNfcOemExtension.setRfCustomPollingFrames(rf_frames);
    }
}
