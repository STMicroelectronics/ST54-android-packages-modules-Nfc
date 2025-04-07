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

import android.nfc.cardemulation.CardEmulation;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NfcSettingsAdapterImpl extends INfcSettingsAdapter.Stub {
    private static final String TAG = "NfcSettingsAdapterImpl";

    private final StNfcOemExtension mStNfcOemExtension;

    public NfcSettingsAdapterImpl(StNfcOemExtension e) {
        mStNfcOemExtension = e;
    }

    @Override
    public IBinder asBinder() {
        return this;
    }

    /**
     * Indicates if an UICC is connected to the ST21NFCD chip.
     *
     * @return true if an UICC is connected, false if not.
     */
    public boolean isUiccConnected() {
        boolean result = false;

        Log.d(TAG, "isUiccConnected - " + result);
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
        Log.d(TAG, "iseSEConnected - " + result);
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

    int convertRouteToInt(String route) {
        if (route.contains(NfcSettingsAdapterImpl.UICC_ROUTE)) {
            return CardEmulation.PROTOCOL_AND_TECHNOLOGY_ROUTE_UICC;
        } else if (route.contains(NfcSettingsAdapterImpl.ESE_ROUTE)) {
            return CardEmulation.PROTOCOL_AND_TECHNOLOGY_ROUTE_ESE;
        } else if (route.contains(NfcSettingsAdapterImpl.HCE_ROUTE)) {
            return CardEmulation.PROTOCOL_AND_TECHNOLOGY_ROUTE_DH;
        } else if (route.contains(NfcSettingsAdapterImpl.DEFAULT_ROUTE)) {
            return CardEmulation.PROTOCOL_AND_TECHNOLOGY_ROUTE_DEFAULT;
        }
        return CardEmulation.PROTOCOL_AND_TECHNOLOGY_ROUTE_UNSET;
    }

    /**
     * Set listen mode routing table configuration for Default Route. routeLoc is parameter which
     * fetch the text from UI and compare *
     *
     * <p>Requires {@link android.Manifest.permission#NFC} permission.
     *
     * @throws IOException If a failure occurred during Default Route Route set.
     */
    public void setDefaultUserRoutes(List<DefaultRouteEntry> userRoutes) {
        Log.i(TAG, "setDefaultUserRoutes: userRoutes=" + userRoutes.toString());
        int routeProto, routeTechno, routeAid, routeSc;
        routeProto = CardEmulation.PROTOCOL_AND_TECHNOLOGY_ROUTE_UNSET;
        routeTechno = CardEmulation.PROTOCOL_AND_TECHNOLOGY_ROUTE_UNSET;
        routeAid = CardEmulation.PROTOCOL_AND_TECHNOLOGY_ROUTE_UNSET;
        routeSc = CardEmulation.PROTOCOL_AND_TECHNOLOGY_ROUTE_UNSET;
        for (DefaultRouteEntry entry : userRoutes) {
            if (entry.getRouteName().contains(NfcSettingsAdapterImpl.DEFAULT_ISO_DEP_ROUTE)) {
                routeProto = convertRouteToInt(entry.getRouteLoc());
            } else if (entry.getRouteName()
                    .contains(NfcSettingsAdapterImpl.DEFAULT_AB_TECH_ROUTE)) {
                routeTechno = convertRouteToInt(entry.getRouteLoc());
            } else if (entry.getRouteName().contains(NfcSettingsAdapterImpl.DEFAULT_AID_ROUTE)) {
                routeAid = convertRouteToInt(entry.getRouteLoc());
            } else if (entry.getRouteName().contains(NfcSettingsAdapterImpl.DEFAULT_SC_ROUTE)) {
                routeSc = convertRouteToInt(entry.getRouteLoc());
            }
        }
        mStNfcOemExtension.overwriteRoutingTable(routeProto, routeTechno, routeAid, routeSc);
    }

    /**
     * Set listen mode routing table configuration for Default Route. routeLoc is parameter which
     * fetch the text from UI and compare *
     *
     * <p>Requires {@link android.Manifest.permission#NFC} permission.
     *
     * @throws IOException If a failure occurred during Default Route Route set.
     */
    public List<DefaultRouteEntry> getDefaultUserRoutes() {
        List<DefaultRouteEntry> userRoutes = new ArrayList<>();

        Log.i(TAG, "getDefaultUserRoutes");

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
    public List<DefaultRouteEntry> getEffectiveRoutes() {
        List<DefaultRouteEntry> userRoutes = new ArrayList<>();

        Log.i(TAG, "getEffectiveRoutes");

        List<DefaultRouteEntry> routeList = mStNfcOemExtension.getRoutingTable();

        return routeList;
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
