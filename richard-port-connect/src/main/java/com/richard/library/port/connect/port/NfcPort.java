package com.richard.library.port.connect.port;


import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;

import java.io.IOException;

/**
 * @author: Richard
 * @createDate: 2026/4/21 13:53
 * @version: 1.0
 * @description: NFC通信
 */
public class NfcPort {

    public NfcAdapter nfcAdapter;
    public PendingIntent pendingIntent;
    private final Context context;
    private Intent intent;
    public Tag tag;
    public MifareClassic mfc;
    public byte[] KEYFORUM;
    public byte[] KEYDEFAULT;

    public NfcPort(Context context) {
        this.KEYFORUM = MifareClassic.KEY_NFC_FORUM;
        this.KEYDEFAULT = MifareClassic.KEY_DEFAULT;
        this.context = context;
        this.nfcAdapter = android.nfc.NfcAdapter.getDefaultAdapter(context);
        this.pendingIntent = android.app.PendingIntent.getActivity(context, 0, (new Intent(context, context.getClass())).addFlags(536870912), 0);
    }

    public void enableNfc() {
        if (this.nfcAdapter != null) {
            this.nfcAdapter.enableForegroundDispatch((Activity) this.context, this.pendingIntent, (IntentFilter[]) null, (String[][]) null);
        }

    }

    public void disableNfc() {
        if (this.nfcAdapter != null) {
            this.nfcAdapter.disableForegroundDispatch((Activity) this.context);
        }

    }

    public boolean checkNfcEnable() {
        return this.nfcAdapter != null && this.nfcAdapter.isEnabled();
    }

    public void putIntent(Intent intent) {
        this.intent = intent;
        this.tag = (Tag) intent.getParcelableExtra("android.nfc.extra.TAG");
        this.mfc = MifareClassic.get(this.tag);
    }

    public byte[] getId() {
        String intentActionStr = this.intent.getAction();
        return !"android.nfc.action.NDEF_DISCOVERED".equals(intentActionStr) && !"android.nfc.action.TECH_DISCOVERED".equals(intentActionStr) && !"android.nfc.action.TAG_DISCOVERED".equals(intentActionStr) ? null : this.tag.getId();
    }

    public boolean connect() {
        if (this.mfc != null) {
            try {
                this.mfc.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;
        }
        return false;
    }

    public String getType() {
        if (this.mfc != null && this.mfc.isConnected()) {
            int type = this.mfc.getType();
            String typeS = "";
            switch (type) {
                case MifareClassic.TYPE_UNKNOWN:
                    typeS = "TYPE_UNKNOWN";
                    break;
                case MifareClassic.TYPE_CLASSIC:
                    typeS = "TYPE_CLASSIC";
                    break;
                case MifareClassic.TYPE_PLUS:
                    typeS = "TYPE_PLUS";
                    break;
                case MifareClassic.TYPE_PRO:
                    typeS = "TYPE_PRO";
            }

            return typeS;
        }
        return null;
    }

    public int getSectorCount() {
        return this.mfc != null && this.mfc.isConnected() ? this.mfc.getSectorCount() : 0;
    }

    public int getBlockCount() {
        return this.mfc != null && this.mfc.isConnected() ? this.mfc.getBlockCount() : 0;
    }

    public int getSize() {
        return this.mfc.isConnected() ? this.mfc.getSize() : 0;
    }

    public int getBlockCountInSector(int Sector) {
        return this.mfc.isConnected() ? this.mfc.getBlockCountInSector(Sector) : 0;
    }

    public int getSectorFirstBlockNum(int Sector) {
        return this.mfc.isConnected() ? this.mfc.sectorToBlock(Sector) : 0;
    }

    public boolean pairKey(byte[] keyA, byte[] keyB, int Sector) throws IOException {
        if (this.mfc != null && this.mfc.isConnected()) {
            if (keyA == null && keyB == null) {
                return false;
            } else {
                if (keyA == null) {
                    return this.mfc.authenticateSectorWithKeyB(Sector, keyB);
                } else if (keyB == null) {
                    return this.mfc.authenticateSectorWithKeyA(Sector, keyA);
                } else {
                    this.mfc.authenticateSectorWithKeyA(Sector, keyA);
                    return this.mfc.authenticateSectorWithKeyB(Sector, keyB);
                }
            }
        }

        return false;
    }

    public boolean write(int block, byte[] data) throws IOException {
        if (this.mfc != null && this.mfc.isConnected()) {
            this.mfc.writeBlock(block, data);
            return true;
        }

        return false;
    }

    public byte[] read(int Block) throws IOException {
        return this.mfc != null && this.mfc.isConnected() ? this.mfc.readBlock(Block) : null;
    }

    public boolean close() throws Exception {
        if (this.mfc != null && this.mfc.isConnected()) {
            this.mfc.close();
            return true;
        }
        return false;
    }
}

