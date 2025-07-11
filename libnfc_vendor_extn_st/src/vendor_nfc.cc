/******************************************************************************
 *
 *  Copyright (C) 2025 STMicroelectronics
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
 ******************************************************************************/
#include <pthread.h>
#include <string.h>

#include <android-base/logging.h>
#include <android-base/stringprintf.h>
#include <android/log.h>
#include <log/log.h>

#include <NfcVendorExtn.h>
#include <nfc_api.h>

#include <stpropnci.h>

using android::base::StringPrintf;

static VendorExtnCb* pVendorExtnCb = nullptr;
static pthread_mutex_t mtx = PTHREAD_MUTEX_INITIALIZER;

/*******************************************************************************
**
** Function         nves_dump
**
** Description      Dump at VERBOSE level the messages in and out of the wrapper
**
** Returns          none
**
*******************************************************************************/
#define DUMP_CHUNK_SZ 100
static void nves_dump(bool upperlayer, bool dir_to_nfcc, uint8_t* payload,
                      uint16_t payloadlen) {
  const char* prefixLayer = upperlayer ? "UL" : "";
  const char* prefixDirFirst = dir_to_nfcc ? "Tx" : "Rx";
  const char* prefixDirCont = dir_to_nfcc ? "tx" : "rx";
  char buf[DUMP_CHUNK_SZ * 3 + 1];
  int offset = 0, i;

  do {
    int len = payloadlen - offset;
    if (len > DUMP_CHUNK_SZ) len = DUMP_CHUNK_SZ;

    for (i = 0; i < len; i++) {
      sprintf(buf + 3 * i, "%02hhx ", payload[offset + i]);
    }
    buf[3 * i - 1] = '\0';  // remove training space

    LOG(VERBOSE) << StringPrintf("%s: %s%s %s", __func__, prefixLayer,
                                 (offset == 0) ? prefixDirFirst : prefixDirCont,
                                 buf);

    offset += len;
  } while (offset < payloadlen);
}

/*******************************************************************************
**
** Function         nves_stpropnci_cb
**
** Description      After processing, we send output message to HAL or
**                  to stack based on the direction
**
** Returns          none
**
*******************************************************************************/
static void nves_stpropnci_cb(bool dir_to_nfcc, uint8_t* payload,
                              uint16_t payloadlen) {
  if (dir_to_nfcc == MSG_DIR_TO_NFCC) {
    /* write to HAL */
    nves_dump(false, true, payload, payloadlen);
    if (pVendorExtnCb->hidlHal != nullptr) {
      LOG(VERBOSE) << StringPrintf("%s: to HIDL");
      ::android::hardware::nfc::V1_0::NfcData data;
      data.setToExternal(payload, payloadlen);
      pVendorExtnCb->hidlHal->write(data);
    } else if (pVendorExtnCb->aidlHal != nullptr) {
      int ret;
      LOG(VERBOSE) << StringPrintf("%s: to AIDL");
      std::vector<uint8_t> aidl_data(payload, payload + payloadlen);
      pVendorExtnCb->aidlHal->write(aidl_data, &ret);
    } else {
      LOG(ERROR) << StringPrintf("%s: no HAL interface available");
    }
  } else {
    /* Emulate cb from HAL */
    nves_dump(true, false, payload, payloadlen);
    if (pVendorExtnCb->pDataCback != nullptr) {
      LOG(VERBOSE) << StringPrintf("%s: to STACK");
      pVendorExtnCb->pDataCback(payloadlen, payload);
    } else {
      LOG(ERROR) << StringPrintf("%s: no HAL cb interface available");
    }
  }
}

/*******************************************************************************
**
** Function         vendor_nfc_handle_event
**
** Description      Handles events or messages.
**                  At the moment we only process messages.
**
** Returns          NFC_STATUS_OK or an error code.
**
*******************************************************************************/
extern "C" tNFC_STATUS vendor_nfc_handle_event(NfcExtEvent_t event,
                                               NfcExtEventData_t data)
    __attribute__((visibility("default")));
extern "C" tNFC_STATUS vendor_nfc_handle_event(NfcExtEvent_t event,
                                               NfcExtEventData_t data) {
  tNFC_STATUS ret = NFC_STATUS_OK;
  if ((event != HANDLE_VENDOR_NCI_MSG) &&
      (event != HANDLE_VENDOR_NCI_RSP_NTF) && (event != HANDLE_HAL_EVENT)) {
    LOG(VERBOSE) << StringPrintf("%s: Enter event: %d", __func__, event);
  }
  (void)pthread_mutex_lock(&mtx);
  if (pVendorExtnCb == nullptr) {
    LOG(ERROR) << "Vendor_nfc not initialized";
    (void)pthread_mutex_unlock(&mtx);
    return NFC_STATUS_REJECTED;
  }

  switch (event) {
    case HANDLE_VENDOR_NCI_MSG:
      // called from processCmd.
      nves_dump(true, true, data.nci_msg.p_data, data.nci_msg.data_len);
      if (stpropnci_process(MSG_DIR_FROM_STACK, data.nci_msg.p_data,
                            data.nci_msg.data_len)) {
        ret = NFCSTATUS_EXTN_FEATURE_SUCCESS;
      }
      break;

    case HANDLE_VENDOR_NCI_RSP_NTF:
      // called from processCmd.
      nves_dump(false, false, data.nci_rsp_ntf.p_data,
                data.nci_rsp_ntf.data_len);
      if (stpropnci_process(MSG_DIR_FROM_NFCC, data.nci_rsp_ntf.p_data,
                            data.nci_rsp_ntf.data_len)) {
        ret = NFCSTATUS_EXTN_FEATURE_SUCCESS;
      }
      break;

    case HANDLE_HAL_EVENT:
      LOG(VERBOSE) << StringPrintf("%s: enter HAL event: %d", __func__,
                                   data.hal_event);
      switch (data.hal_event) {
        case HAL_NFC_OPEN_CPLT_EVT:
          if (pVendorExtnCb->aidlHal != nullptr) {
            int32_t halVer = 0;
            pVendorExtnCb->aidlHal->getInterfaceVersion(&halVer);
            LOG(DEBUG) << StringPrintf("%s: HAL AIDL v%d", __func__, halVer);
            if (halVer >= 2) {
              stpropnci_st_set_hal_passthrough();
            }
          }
          break;

        default:
          // no special handling
          break;
      }
      break;

    default:
      // vendor_nfc_handle_event is never called with other actions at the
      // moment..
      LOG(DEBUG) << StringPrintf("Unexpected event: %d", event);
      break;
  }

  (void)pthread_mutex_unlock(&mtx);
  return ret;
}

/*******************************************************************************
**
** Function         vendor_nfc_on_config_update
**
** Description      Handles updates in config structure.
**                  We don't use this at the moment.
**
** Returns          none
**
*******************************************************************************/
extern "C" void vendor_nfc_on_config_update(std::map<std::string, ConfigValue>*)
    __attribute__((visibility("default")));
extern "C" void vendor_nfc_on_config_update(
    std::map<std::string, ConfigValue>*) {
  LOG(VERBOSE) << StringPrintf("%s: enter ", __func__);
}

/*******************************************************************************
**
** Function         vendor_nfc_init
**
** Description      Manages initialization of this module.
**
** Returns          true if the init is successful and module can be used.
**
*******************************************************************************/
extern "C" bool vendor_nfc_init(VendorExtnCb* cb)
    __attribute__((visibility("default")));

extern "C" bool vendor_nfc_init(VendorExtnCb* cb) {
  bool ret = true;
  LOG(DEBUG) << StringPrintf("%s: enter", __func__);

  (void)pthread_mutex_lock(&mtx);

  // Save the control block.
  pVendorExtnCb = cb;

  // Initialize the processor lib
  if (!stpropnci_init(2, nves_stpropnci_cb)) {
    LOG(ERROR) << StringPrintf("%s: Failed to init stpropnci", __func__);
    ret = false;
  }

  (void)pthread_mutex_unlock(&mtx);
  return ret;
}

/*******************************************************************************
**
** Function         vendor_nfc_de_init
**
** Description      Manages de-initialization of this module.
**
** Returns          none
**
*******************************************************************************/
extern "C" bool vendor_nfc_de_init() __attribute__((visibility("default")));
extern "C" bool vendor_nfc_de_init() {
  (void)pthread_mutex_lock(&mtx);
  stpropnci_deinit();
  pVendorExtnCb = nullptr;
  (void)pthread_mutex_unlock(&mtx);
  return true;
}
