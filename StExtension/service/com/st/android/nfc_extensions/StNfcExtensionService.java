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

// import android.app.Notification;
// import android.app.NotificationChannel;
// import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

// import androidx.core.app.NotificationCompat;
// import androidx.core.app.ServiceCompat;

public class StNfcExtensionService extends Service {

    private static final String TAG = "StNfcExtensionService";

    private StNfcOemExtension mStNfcOemExtension;

    private INfcAdapterStExtensions.Stub mINfcAdapterStExtensionsBinder;
    private NfcAdapterStExtensionsImpl mNfcAdapterStExtensionsImpl;

    private class srvStNfcOemExtensionVendorNtfCallback
            implements StNfcOemExtension.StNfcOemExtensionVendorNtfCallback {
        public void onVendorNciNotification(int gid, int oid, byte[] payload) {
            Log.d(
                    TAG,
                    "srvStNfcOemExtensionVendorNtfCallback:  g="
                            + Integer.toHexString(gid)
                            + ", o="
                            + Integer.toHexString(oid)
                            + ", payload="
                            + StNfcOemExtension.bytesToString(payload));
            // Do nothing at the moment
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mStNfcOemExtension = new StNfcOemExtension();
        mNfcAdapterStExtensionsImpl = new NfcAdapterStExtensionsImpl(mStNfcOemExtension);
        mINfcAdapterStExtensionsBinder = mNfcAdapterStExtensionsImpl;
        mStNfcOemExtension.register(this, new srvStNfcOemExtensionVendorNtfCallback());
    }

    @Override
    public void onDestroy() {
        mStNfcOemExtension.unregister();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand()");
        // try {
        //     String CHANNEL_ID = "StExtChannel";
        //     CharSequence name = getString(R.string.channel_name);
        //     String description = getString(R.string.channel_description);
        //     int importance = NotificationManager.IMPORTANCE_NONE;
        //     NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        //     channel.setDescription(description);
        //     // Register the channel with the system; you can't change the importance
        //     // or other notification behaviors after this.
        //     NotificationManager notificationManager =
        // getSystemService(NotificationManager.class);
        //     notificationManager.createNotificationChannel(channel);

        //     Notification notification =
        //             new NotificationCompat.Builder(this, CHANNEL_ID)
        //                     .setContentTitle("StNfcExtensions")
        //                     // Create the notification to display while the service
        //                     // is running
        //                     .build();
        //     int type = ServiceInfo.FOREGROUND_SERVICE_TYPE_SYSTEM_EXEMPTED;

        //     ServiceCompat.startForeground(
        //             /* service= */ this,
        //             /* id= */ 100, // Cannot be 0
        //             /* notification= */ notification,
        //             /* foregroundServiceType= */ type);
        // } catch (Exception e) {
        //     Log.e(TAG, "Couldn t start service: " + e);
        // }
        mStNfcOemExtension.doTaskBoot();
        return Service.START_STICKY; // super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Return the interface to SDK.
        Log.i(TAG, "onBind()");
        return mINfcAdapterStExtensionsBinder;
    }
}
