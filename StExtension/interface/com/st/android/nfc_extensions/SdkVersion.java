/*
 * Copyright (C) 2025 ST Microelectronics S.A.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at:
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  Provide extensions for the ST implementation of the NFC stack
 */
package com.st.android.nfc_extensions;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class SdkVersion implements Parcelable {
    /* This version will be updated when AIDL is changed */
    public static int CURRENT_API_VERSION = 2;

    /* The remaining should be constant */

    static String TAG = "NfcSdkVersion";
    public static int FLAVOUR_GENERAL = 0;
    public static int FLAVOUR_PRC = 1;
    public static int FLAVOUR_OEM_SPECIFIC = 2;

    public static int CURRENT_FLAVOUR = FLAVOUR_GENERAL;

    private final int mFlavour;
    private final int mVersion;

    /* constructor */
    public SdkVersion(int flavour, int version) {
        this.mFlavour = flavour;
        this.mVersion = version;
    }

    public SdkVersion() {
        this.mFlavour = CURRENT_FLAVOUR;
        this.mVersion = CURRENT_API_VERSION;
    }

    public int getVersion() {
        return this.mVersion;
    }

    public int getFlavour() {
        return this.mFlavour;
    }

    /* check the compabitility */
    public static boolean checkSdkCompatibility(SdkVersion service, SdkVersion client) {
        if (client.getFlavour() != FLAVOUR_GENERAL) {
            if (client.getFlavour() != service.getFlavour()) {
                Log.d(
                        TAG,
                        "checkCompatibility failed: Client specific features not present in"
                                + " service");
                return false;
            }
        }

        if (client.getVersion() > service.getVersion()) {
            Log.d(TAG, "checkCompatibility failed: Client API version > service API version");
            return false;
        }

        /* other cases should be compatible */
        return true;
    }

    /* for AIDL compatibility */
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mFlavour);
        dest.writeInt(mVersion);
    }

    public static final Parcelable.Creator<SdkVersion> CREATOR =
            new Parcelable.Creator<SdkVersion>() {
                @Override
                public SdkVersion createFromParcel(Parcel source) {
                    int f = source.readInt();
                    int v = source.readInt();
                    return new SdkVersion(f, v);
                }

                @Override
                public SdkVersion[] newArray(int size) {
                    return new SdkVersion[size];
                }
            };
}
