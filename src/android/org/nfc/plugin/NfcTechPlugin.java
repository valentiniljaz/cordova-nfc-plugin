package org.nfc.plugin;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcV;
import android.nfc.Tag;

import org.json.JSONArray;
import org.json.JSONException;

public class NfcTechPlugin extends CordovaPlugin {

    private  NfcAdapter mNfcAdapter;

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
        if("readNfc".equals("action")){
            return readNfc(callbackContext);
        }
        return false;
    }
    private boolean readNfc(CallbackContext callbackContext){
        cordova.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

                if (mNfcAdapter == null) {
                    // Stop here, we definitely need NFC
                    callbackContext.error("This device doesn't support NFC");
                    return true;
                }

                if (!mNfcAdapter.isEnabled()) {
                    callbackContext.error("NFC is disabled");
                    return true;
                }
                return handleIntent(getIntent(), callbackContext);
            }
        });
        return false;
    }
    private boolean handleIntent(Intent intent) {
        setupForegroundDispatch(this, mNfcAdapter);
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            Tag mTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            byte[] id = mTag.getId();
            callbackContext.success(bytesToHex(id));
            stopForegroundDispatch(this, mNfcAdapter);
            return true;
        }
    }

    /**
     * @param activity The corresponding {@link Activity} requesting the foreground dispatch.
     * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter filter = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        String[][] techList = new String[][]{new String [] {NfcV.class.getName()}};
        try {
            filter.addDataType("*/*");
        }catch (IntentFilter.MalformedMimeTypeException e){
            throw new RuntimeException("failed", e);
        }
        IntentFilter[] filters = new IntentFilter[]{filter};
        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }
    public static String bytesToHex(byte[] bytes){
        final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        char[] hexChars = new char[bytes.length * 2];
        int v;
        for(int j=0; j<bytes.length; j++){
            v = bytes[j] & 0xFF;
            hexChars[j*2] = hexArray[v >>> 4];
            hexChars[j*2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}