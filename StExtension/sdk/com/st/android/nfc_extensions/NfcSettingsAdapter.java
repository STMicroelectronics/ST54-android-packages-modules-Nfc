/*
 *  The original Work has been changed by ST Microelectronics S.A.
 *
 * Copyright MediaTek Inc. (C) 2017
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.st.android.nfc_extensions;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated
public class NfcSettingsAdapter {
    private static INfcSettingsAdapter mNfcSettingsInterface = null;

    private static final String TAG = "NfcSettingsAdapter";

    // final Context mContext;

    /* Below values must be aligned with SecureElementSelector code */
    public static final String SE_SIM1 = "SIM1";
    public static final String SE_SIM2 = "SIM2";
    public static final String SE_ESE1 = "eSE";

    public static final String SE_STATE_ACTIVATED = "Active";
    public static final String SE_STATE_AVAILABLE = "Available";
    public static final String SE_STATE_NOT_AVAILABLE = "N/A";

    public NfcSettingsAdapter(INfcSettingsAdapter intf) {
        mNfcSettingsInterface = intf;
    }

    /**
     * Helper to get the default NFC Settings Adapter.
     *
     * @param context the calling application's context
     * @return the default NFC settings adapter, or null if no NFC settings adapter exists
     */
    public static NfcSettingsAdapter getDefaultAdapter(Context context) {
        Log.e(TAG, "NfcSettingsAdapter is deprecated");

        return null;
    }

    /**
     * Indicates if an UICC is connected to the ST21NFCD chip.
     *
     * @return true if an UICC is connected, false if not.
     */
    public boolean isUiccConnected() {
        boolean result = false;

        Log.d(TAG, "isUiccConnected:" + result);
        Log.e(TAG, "NfcSettingsAdapter is deprecated");

        return result;
    }

    /**
     * Indicates if an eSE is connected to the ST21NFCD chip.
     *
     * @return true if an eSE is connected, false if not.
     */
    public boolean iseSEConnected() {
        boolean result = false;
        Log.d(TAG, "iseSEConnected:" + result);
        Log.e(TAG, "NfcSettingsAdapter is deprecated");

        return result;
    }

    /**
     * Indicates if an SE given it HostID is connected to the ST21NFCD chip.
     *
     * @return true if an SE is connected, false if not.
     */
    public boolean isSEConnected(int HostID) {
        boolean result = false;
        Log.d(TAG, "isSEConnected(" + HostID + ") - " + result);
        Log.e(TAG, "NfcSettingsAdapter is deprecated");

        return result;
    }

    /**
     * This API activate or deactivate the given Secure Element defined by se_id.
     *
     * <p>
     *
     * @return true if successful
     */
    public boolean EnableSE(String se_id, boolean enable) {
        Log.i(TAG, "EnableSE(" + se_id + ", " + enable + ")");
        boolean status = false;

        Log.e(TAG, "NfcSettingsAdapter is deprecated");

        return status;
    }

    /*
     * Get the current state of SWP elements:
     * Each item is a pair NAME:STATUS.
     * NAME and STATUS are constants NfcSettingsAdapter.SE_*
     * NAME is one of: SIM1, SIM2, eSE.
     * STATUS is one of: Active, Available, N/A.
     */
    public List<String> getSecureElementsStatus() {
        Log.i(TAG, "getSecureElementsStatus");

        Log.e(TAG, "NfcSettingsAdapter is deprecated");

        return null;
    }

    /* Callback for UI updates */
    public void registerNfcSettingsCallback(INfcSettingsCallback cb) {
        Log.i(TAG, "registerNfcSettingsCallback");

        Log.e(TAG, "NfcSettingsAdapter is deprecated");
    }

    public void unregisterNfcSettingsCallback(INfcSettingsCallback cb) {
        Log.i(TAG, "unregisterNfcSettingsCallback");

        Log.e(TAG, "NfcSettingsAdapter is deprecated");
    }

    public static final String DEFAULT_AID_ROUTE = "default_aid_route";
    public static final String DEFAULT_MIFARE_ROUTE = "default_mifare_route";
    public static final String DEFAULT_ISO_DEP_ROUTE = "default_iso_dep_route";
    public static final String DEFAULT_FELICA_ROUTE = "default_felica_route";
    public static final String DEFAULT_AB_TECH_ROUTE = "default_ab_tech_route";
    public static final String DEFAULT_SC_ROUTE = "default_sc_route";

    public static final String UICC_ROUTE = "UICC";
    public static final String ESE_ROUTE = "eSE";
    public static final String HCE_ROUTE = "HCE";
    public static final String DEFAULT_ROUTE = "Default";
    public static final String UNSET_ROUTE = "Unset";

    /**
     * Set listen mode routing table configuration for Default Route. routeLoc is parameter which
     * fetch the text from UI and compare *
     *
     * <p>Requires {@link android.Manifest.permission#NFC} permission.
     *
     * @throws IOException If a failure occurred during Default Route Route set.
     */
    public void DefaultRouteSet(String routeLoc) throws IOException {
        Log.i(TAG, "DefaultRouteSet() - route: " + routeLoc);

        Log.e(TAG, "NfcSettingsAdapter is deprecated");
    }

    /**
     * Set listen mode routing table configuration for Default Route. routeLoc is parameter which
     * fetch the text from UI and compare *
     *
     * <p>Requires {@link android.Manifest.permission#NFC} permission.
     *
     * @throws IOException If a failure occurred during Default Route Route set.
     */
    public void setUserDefaultRoutes(Map<String, String> routeList) throws IOException {
        List<DefaultRouteEntry> defaultRouteList = new ArrayList<DefaultRouteEntry>();

        for (Map.Entry<String, String> entry : routeList.entrySet()) {
            String routeKey = entry.getKey();
            String routeValue = entry.getValue();

            Log.d(TAG, "setUserDefaultRoutes:" + routeKey + ": " + routeValue);

            if ((DEFAULT_MIFARE_ROUTE.contentEquals(routeKey) == true)
                    || (DEFAULT_FELICA_ROUTE.contentEquals(routeKey) == true)) {
                Log.w(TAG, "setUserDefaultRoutes:" + routeKey + " is deprecated");
                continue;
            }

            if ((DEFAULT_AID_ROUTE.contentEquals(routeKey) == false)
                    && (DEFAULT_ISO_DEP_ROUTE.contentEquals(routeKey) == false)
                    // && (DEFAULT_FELICA_ROUTE.contentEquals(routeKey) == false)
                    && (DEFAULT_AB_TECH_ROUTE.contentEquals(routeKey) == false)
                    && (DEFAULT_SC_ROUTE.contentEquals(routeKey) == false)) {
                Log.e(TAG, "setUserDefaultRoutes:" + routeKey + " does not exists");
                throw new IOException(routeKey + " does not exists");
            }

            if ((UICC_ROUTE.contentEquals(routeValue) == false)
                    && (ESE_ROUTE.contentEquals(routeValue) == false)
                    && (HCE_ROUTE.contentEquals(routeValue) == false)
                    && (DEFAULT_ROUTE.contentEquals(routeValue) == false)
                    && (UNSET_ROUTE.contentEquals(routeValue) == false)) {
                Log.e(TAG, "setUserDefaultRoutes:" + routeValue + " does not exists");
                throw new IOException(routeValue + " does not exists");
            }

            DefaultRouteEntry defaultRouteEntry = new DefaultRouteEntry(routeKey, routeValue);
            defaultRouteList.add(defaultRouteEntry);
        }
        try {
            mNfcSettingsInterface.setDefaultUserRoutes(defaultRouteList);
        } catch (Exception e) {
            Log.e(TAG, "setUserDefaultRoutes: Exception e=" + e.toString());
        }
    }

    /**
     * Set listen mode routing table configuration for Default Route. routeLoc is parameter which
     * fetch the text from UI and compare *
     *
     * <p>Requires {@link android.Manifest.permission#NFC} permission.
     *
     * @throws IOException If a failure occurred during Default Route Route set.
     */
    public Map<String, String> getUserDefaultRoutes() throws IOException {
        Map<String, String> userRoutes = new HashMap<String, String>();

        Log.i(TAG, "getUserDefaultRoutes");

        Log.e(TAG, "NfcSettingsAdapter is deprecated");

        return userRoutes;
    }

    /**
     * Set listen mode routing table configuration for Default Route. routeLoc is parameter which
     * fetch the text from UI and compare *
     *
     * <p>Requires {@link android.Manifest.permission#NFC} permission.
     *
     * @throws IOException If a failure occurred during Default Route Route set.
     */
    public Map<String, String> getEffectiveDefaultRoutes() throws IOException {
        Map<String, String> userRoutes = new HashMap<String, String>();
        List<DefaultRouteEntry> routeList = null;
        Log.i(TAG, "getEffectiveDefaultRoutes");

        try {
            routeList = mNfcSettingsInterface.getEffectiveRoutes();
            for (DefaultRouteEntry entry : routeList) {
                userRoutes.put(entry.getRouteName(), entry.getRouteLoc());
            }
        } catch (Exception e) {
            Log.e(TAG, "getEffectiveDefaultRoutes: Exception e=" + e.toString());
        }

        return userRoutes;
    }

    /**
     * Retrieve how many bytes are still availabe to add AID entries in the listen mode routing
     * table.
     *
     * <p>Each entry has an overhead of 4 bytes per AID. Entries for the same route as the default
     * route don't consume space. The available space can change if the default route changes.
     *
     * <p>In case of overflow, this method returns 0.
     *
     * <p>In case of problem, returns -1
     *
     * @return
     */
    public int getAvailableSpaceForAid() {
        Log.i(TAG, "getAvailableSpaceForAid");

        Log.e(TAG, "NfcSettingsAdapter is deprecated");
        return -1;
    }
}
