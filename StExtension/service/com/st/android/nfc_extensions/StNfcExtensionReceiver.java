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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcOemExtension;
import android.util.Log;

import java.util.Objects;

public class StNfcExtensionReceiver extends BroadcastReceiver {
    private static final String TAG = "StNfcExtensionReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), NfcOemExtension.ACTION_OEM_EXTENSION_INIT)) {
            Log.i(TAG, "onReceive() - ACTION_OEM_EXTENSION_INIT");
            Intent serviceIntent = new Intent(context, StNfcExtensionService.class);
            context.startService(serviceIntent);
        } else {
            Log.i(TAG, "onReceive() other: " + intent);
        }
    }
}
