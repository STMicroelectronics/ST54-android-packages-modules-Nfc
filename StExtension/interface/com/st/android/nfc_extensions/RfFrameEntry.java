package com.st.android.nfc_extensions;

import android.util.Log;

import java.util.Arrays;

public class RfFrameEntry {

    int position;
    int rsp_type;
    int frame_type;
    boolean crc;
    int waiting_time;
    byte[] frame;

    private static final String TAG = "Nfc_RfFrameEntry";

    public static final int CUST_POLL_NO_RESP = 0x00;
    public static final int CUST_POLL_STD_RESP = 0x01;
    public static final int CUST_POLL_NOSTD_RESP = 0x02;

    public static final int NFA_A_FRAME = 0x00;
    public static final int NFA_B_FRAME = 0x01;
    public static final int NFA_A_SHORT_FRAME = 0x02;
    public static final int NFA_F_FRAME = 0x02;
    public static final int NFA_V_FRAME = 0x03;
    public static final int NFA_B_FRAME_NO_SOF_EOF = 0x04;
    public static final int NFA_B_FRAME_NO_SOF = 0x05;

    public RfFrameEntry(int pos, int rt, int type, boolean check, int wt, byte[] data)
            throws Exception {
        if (pos > 4) {
            throw new Exception("position must be < 4");
        }
        position = pos;
        if (rt <= 2) {
            if (type > 2) {
                throw new Exception("type must be <= 2");
            }
        } else if (rt == 3) {
            if (type > 5) {
                throw new Exception("type must be <= 5");
            }
        } else {
            throw new Exception("response type must be <= 3");
        }
        rsp_type = rt;
        frame_type = type;
        waiting_time = wt;
        crc = check;
        if (data == null) {
            throw new Exception("frame is null");
        }
        if (data.length == 0) {
            throw new Exception("frame length is 0");
        }
        frame = Arrays.copyOf(data, data.length);
        Log.i(
                TAG,
                "RfFrameEntry(constructor): position="
                        + pos
                        + ", rsp_type="
                        + rsp_type
                        + ", frame_type="
                        + type
                        + ", crc="
                        + check
                        + ", waiting_time="
                        + wt
                        + ", frame="
                        + bytesToString(frame));
    }

    byte[] getRfFrameAsTlv() {
        byte[] data = new byte[3 + frame.length];
        data[0] = (byte) ((position << 5) | (rsp_type << 3) | frame_type);
        data[1] = (byte) (frame.length + 1);
        data[2] = (byte) (((crc ? 0x01 : 0x00) << 7) | waiting_time);
        System.arraycopy(frame, 0, data, 3, frame.length);
        Log.i(TAG, "getRfFrameAsTlv: tlv=" + bytesToString(data));
        return data;
    }

    public static String bytesToString(byte[] bytes) {
        if (bytes == null) return "";

        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02X", b & 0xFF));

        return sb.toString();
    }
}
