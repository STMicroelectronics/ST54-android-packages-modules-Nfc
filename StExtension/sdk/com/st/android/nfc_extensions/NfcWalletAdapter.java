/*
 * Copyright (C) 2019 ST Microelectronics S.A.
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

import android.os.RemoteException;
import android.util.Log;

public final class NfcWalletAdapter {
    private static final String TAG = "NfcWalletAdapter";

    private static INfcWalletAdapter mNfcWalletInterface = null;

    /**
     * Constructor for the {@link NfcWalletAdapter}
     *
     * @param intf a {@link INfcWalletAdapter}, must not be null
     * @return
     */
    public NfcWalletAdapter(INfcWalletAdapter intf) {
        mNfcWalletInterface = intf;
    }

    public boolean keepEseSwpActive(boolean enable) throws RemoteException {
        if (mNfcWalletInterface == null) {
            throw new RemoteException("Disconnected from service");
        }
        boolean result = false;
        result = mNfcWalletInterface.keepEseSwpActive(enable);
        return result;
    }

    public boolean setMuteTech(boolean muteA, boolean muteB, boolean muteF) throws RemoteException {
        if (mNfcWalletInterface == null) {
            throw new RemoteException("Disconnected from service");
        }
        boolean result = false;
        result = mNfcWalletInterface.setMuteTech(muteA, muteB, muteF);
        return result;
    }

    public boolean setObserverMode(boolean enable) throws RemoteException {
        if (mNfcWalletInterface == null) {
            throw new RemoteException("Disconnected from service");
        }
        boolean result = false;
        result = mNfcWalletInterface.setObserverMode(enable);
        return result;
    }

    @Deprecated
    public boolean registerStLogCallback(INfcWalletLogCallback cb) {
        boolean result = false;
        Log.i(TAG, "registerStLogCallback");

        // do we need this ?
        Log.e(TAG, "not supported yet");

        return result;
    }

    @Deprecated
    public boolean unregisterStLogCallback() {
        boolean result = false;
        Log.i(TAG, "unregisterStLogCallback");

        // do we need this ?
        Log.e(TAG, "not supported yet");

        return result;
    }

    @Deprecated
    public boolean rotateRfParameters(boolean reset) {
        boolean result = false;
        Log.i(TAG, "rotateRfParameters");

        // DEPRECATED
        Log.e(TAG, "not supported anymore");
        return result;
    }

    @Deprecated
    public boolean setSEFelicaCardEnabled(boolean status) throws RemoteException {
        // DEPRECATED
        Log.e(TAG, "not supported anymore");
        return false;
    }

    public boolean registerNfceeActionNtfCallback(INfceeActionNtfCallback cb)
            throws RemoteException {
        if (mNfcWalletInterface == null) {
            throw new RemoteException("Disconnected from service");
        }
        boolean result = false;
        result = mNfcWalletInterface.registerNfceeActionNtfCallback(cb);
        return result;
    }

    public boolean unregisterNfceeActionNtfCallback() throws RemoteException {
        if (mNfcWalletInterface == null) {
            throw new RemoteException("Disconnected from service");
        }
        boolean result = false;
        result = mNfcWalletInterface.unregisterNfceeActionNtfCallback();
        return result;
    }

    public boolean registerIntfActivatedNtfCallback(IIntfActivatedNtfCallback cb)
            throws RemoteException {
        if (mNfcWalletInterface == null) {
            throw new RemoteException("Disconnected from service");
        }
        boolean result = false;
        result = mNfcWalletInterface.registerIntfActivatedNtfCallback(cb);
        return result;
    }

    public boolean unregisterIntfActivatedNtfCallback() throws RemoteException {
        if (mNfcWalletInterface == null) {
            throw new RemoteException("Disconnected from service");
        }
        boolean result = false;
        result = mNfcWalletInterface.unregisterIntfActivatedNtfCallback();
        return result;
    }

    public boolean setForceSAK(boolean enabled, int sak) throws RemoteException {
        if (mNfcWalletInterface == null) {
            throw new RemoteException("Disconnected from service");
        }
        boolean result = false;
        result = mNfcWalletInterface.setForceSAK(enabled, sak);
        return result;
    }

    public boolean seteSEInCardSwitching(boolean inswitching) throws RemoteException {
        if (mNfcWalletInterface == null) {
            throw new RemoteException("Disconnected from service");
        }
        boolean result = false;
        result = mNfcWalletInterface.seteSEInCardSwitching(inswitching);
        return result;
    }

    public boolean seteSEInCardSwitchingExt(boolean inswitching, int nbOp) throws RemoteException {
        if (mNfcWalletInterface == null) {
            throw new RemoteException("Disconnected from service");
        }
        boolean result = false;
        result = mNfcWalletInterface.seteSEInCardSwitchingExt(inswitching, nbOp);
        return result;
    }

    /** *************************** RAW mode for ISO14443-3 *************************** */
    public boolean registerRawRfAuthCallback(INfcWalletRawCallback cb) throws RemoteException {
        if (mNfcWalletInterface == null) {
            throw new RemoteException("Disconnected from service");
        }
        boolean result = false;
        result = mNfcWalletInterface.registerRawRfAuthCallback(cb);
        return result;
    }

    public boolean unregisterRawRfAuthCallback() throws RemoteException {
        if (mNfcWalletInterface == null) {
            throw new RemoteException("Disconnected from service");
        }
        boolean result = false;
        result = mNfcWalletInterface.unregisterRawRfAuthCallback();
        return result;
    }

    public boolean rawRfAuth(int duration) throws RemoteException {
        if (mNfcWalletInterface == null) {
            throw new RemoteException("Disconnected from service");
        }
        boolean result = false;
        result = mNfcWalletInterface.rawRfAuth(duration);
        return result;
    }

    public byte[] rawSeAuth(byte[] data) throws RemoteException {
        if (mNfcWalletInterface == null) {
            throw new RemoteException("Disconnected from service");
        }
        byte[] result = null;
        result = mNfcWalletInterface.rawSeAuth(data);
        return result;
    }

    public boolean rawRfMode(boolean enable) throws RemoteException {
        if (mNfcWalletInterface == null) {
            throw new RemoteException("Disconnected from service");
        }
        boolean result = false;
        result = mNfcWalletInterface.rawRfMode(enable);
        return result;
    }

    public byte[] rawJniSeq(int i, byte[] ba) throws RemoteException {
        if (mNfcWalletInterface == null) {
            throw new RemoteException("Disconnected from service");
        }
        byte[] result = null;
        result = mNfcWalletInterface.rawJniSeq(i, ba);
        return result;
    }

    /**
     * This API return the content of the NFC snoop buffer
     *
     * <p>
     *
     * @return content of NFC snoop buffer.
     */
    @Deprecated
    public byte[] getLogBuffer() {
        Log.i(TAG, "getLogBuffer");

        // do we need this ?
        Log.e(TAG, "not supported yet");

        return null;
    }

    /* Readers polling loop spy support */
    @Deprecated
    public boolean registerPollingLoopCallback(INfcWalletPollingLoopCallback cb) {
        boolean result = false;
        Log.i(TAG, "registerPollingLoopCallback");

        // DEPRECATED ==> use AOSP feature for observer mode. To be discussed if not
        // enough.
        Log.e(TAG, "not supported anymore");

        return result;
    }

    @Deprecated
    public boolean unregisterPollingLoopCallback() {
        boolean result = false;
        Log.i(TAG, "unregisterPollingLoopCallback");

        // DEPRECATED ==> use AOSP feature for observer mode. To be discussed if not
        // enough.
        Log.e(TAG, "not supported anymore");
        return result;
    }

    public boolean registerCeApduCallback(INfcWalletCeApduCallback cb) throws RemoteException {
        if (mNfcWalletInterface == null) {
            throw new RemoteException("Disconnected from service");
        }
        boolean result = false;
        result = mNfcWalletInterface.registerCeApduCallback(cb);
        return result;
    }

    public boolean unregisterCeApduCallback() throws RemoteException {
        if (mNfcWalletInterface == null) {
            throw new RemoteException("Disconnected from service");
        }
        boolean result = false;
        result = mNfcWalletInterface.unregisterCeApduCallback();
        return result;
    }

    // public boolean openApduGate() throws RemoteException {
    //     if (mNfcWalletInterface == null) {
    //         throw new RemoteException("Disconnected from service");
    //     }
    //     return mNfcWalletInterface.openApduGate();
    // }

    // public byte[] transceiveApduGate(byte[] data) throws RemoteException {
    //     if (mNfcWalletInterface == null) {
    //         throw new RemoteException("Disconnected from service");
    //     }
    //     byte[] result = null;
    //     result = mNfcWalletInterface.transceiveApduGate(data);
    //     return result;
    // }

    // public void closeApduGate() throws RemoteException {
    //     if (mNfcWalletInterface == null) {
    //         throw new RemoteException("Disconnected from service");
    //     }
    //     mNfcWalletInterface.closeApduGate();
    // }

    public boolean setRfCustomPollingFrames(RfFrameEntry[] rf_frames) throws RemoteException {
        if (mNfcWalletInterface == null) {
            throw new RemoteException("Disconnected from service");
        }
        if (rf_frames.length > 4) {
            throw new RemoteException("Too many RF frames, max 4");
        }
        if ((rf_frames == null) || (rf_frames.length == 0)) {
            Log.i(TAG, "setRfCustomPollingFrames: disable");
            byte[] data = new byte[] {(byte) 0x00};
            mNfcWalletInterface.setRfCustomPollingFrames(data);
        } else {
            int payload_len = 1;
            for (int i = 0; i < rf_frames.length; i++) {
                payload_len += rf_frames[i].getRfFrameAsTlv().length;
            }
            Log.i(TAG, "setRfCustomPollingFrames: enable, nb frames=" + rf_frames.length);
            byte[] payload = new byte[payload_len];
            payload[0] = (byte) rf_frames.length;
            int idx = 1;
            for (int i = 0; i < rf_frames.length; i++) {
                byte[] tlv = rf_frames[i].getRfFrameAsTlv();
                System.arraycopy(tlv, 0, payload, idx, tlv.length);
                idx += tlv.length;
            }
            return mNfcWalletInterface.setRfCustomPollingFrames(payload);
        }
        return false;
    }
}
