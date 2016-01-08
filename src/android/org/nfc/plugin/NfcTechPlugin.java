package org.nfc.plugin;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.PluginResult;
import org.apache.cordova.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcV;
import android.nfc.Tag;

import android.widget.Toast;

public class NfcTechPlugin extends CordovaPlugin {

    private NfcAdapter nfcAdapter;
	private CallbackContext callbackContext;
	
    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;
		if("readNfcTech".equals(action)){
            return readNfc(callbackContext);
        }
		if("checkNfc".equals(action)){
			return checkNfc(callbackContext);
		}
        return false;
    }
	private boolean nfcIsAvailable(NfcAdapter nfcAdapter){
		if (nfcAdapter == null || !nfcAdapter.isEnabled()) {
			return false;
		}
		return true;
	}
	private boolean checkNfc(final CallbackContext callbackContext){
		nfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());
		if (nfcIsAvailable(nfcAdapter)) {
			callbackContext.sucess("NFC available!");
		}else{
			Toast.makeText(getActivity().getApplicationContext(),
                    "This device doesn't support NFC or NFC is disabled!", Toast.LENGTH_SHORT).show();
			callbackContext.error("This device doesn't support NFC or NFC is disabled!");
		}
        return true;
	}
    private boolean readNfc(final CallbackContext callbackContext){
		nfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());
		if(nfcIsAvailable(nfcAdapter)){
			setupForegroundDispatch(getActivity(), nfcAdapter);	
			handleIntent(getIntent());
		}
        return true;
    }
	
	@Override
    public void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            Tag mTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            byte[] id = mTag.getId();
			PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, bytesToHex(id));
			pluginResult.setKeepCallback(true);
			callbackContext.sendPluginResult(pluginResult);
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
    private Activity getActivity() {
        return this.cordova.getActivity();
    }

    private Intent getIntent() {
        return getActivity().getIntent();
    }
}