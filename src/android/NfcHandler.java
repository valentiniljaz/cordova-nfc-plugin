package nfc.plugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.apache.cordova.*;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcV;
import android.nfc.Tag;

public class NfcHandler {
    private static final String STATUS_NFC_OK = "NFC_OK";
    private static final String STATUS_NO_NFC = "NO_NFC";
    private static final String STATUS_NFC_DISABLED = "NFC_DISABLED";
    private static final String STATUS_NFC_STOPPED = "NFC_STOPPED";

    private NfcAdapter nfcAdapter;
    private Activity activity;
	private CallbackContext callbackContext;
    private boolean isListening;

    public NfcHandler(Activity activity, final CallbackContext callbackContext){
        this.activity = activity;
        this.callbackContext = callbackContext;
    }

    public void checkNfcAvailibility(){
		nfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());
		if (nfcAdapter == null) {
			callbackContext.error(STATUS_NO_NFC);
		}else if (!nfcAdapter.isEnabled()){
			callbackContext.error(STATUS_NFC_DISABLED);
        }else {
			callbackContext.success(STATUS_NFC_OK);
        }
	}
    public void startReadingNfc(){
		nfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());
		if (nfcAdapter == null) {
			callbackContext.error(STATUS_NO_NFC);
		}else if (!nfcAdapter.isEnabled()){
			callbackContext.error(STATUS_NFC_DISABLED);
        }else {
			this.isListening = true;
			setupForegroundDispatch(getActivity(), nfcAdapter);
        }
    }
    public void stopReadingNfc(){
		try{
			this.isListening = false;
			stopForegroundDispatch(getActivity(), nfcAdapter);
			callbackContext.success(STATUS_NFC_STOPPED);
		}catch(IllegalStateException e){
			callbackContext.error(e.getMessage());
		}
    }

    public void newIntent(Intent intent) {
        String action = intent.getAction();
        if (this.isListening && NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            handleNfcIntent(intent);
        }
    }
	private void handleNfcIntent(Intent intent) {
		Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		byte[] id = tag.getId();
		
		PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, bytesToHex(id));
		callbackContext.sendPluginResult(pluginResult);

        this.isListening = false;
		//stopForegroundDispatch(getActivity(), nfcAdapter);
	}
    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter filter = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        String[][] techList = new String[][]{new String [] {NfcV.class.getName()}};
        try {
            filter.addDataType("*/*");
        }catch (IntentFilter.MalformedMimeTypeException e){
            throw new RuntimeException("ERROR", e);
        }
        IntentFilter[] filters = new IntentFilter[]{filter};
        adapter.enableForegroundDispatch(activity, pendingIntent, null, null);
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
    private Activity getActivity() { return this.activity; }

    private Intent getIntent() {
        return this.activity.getIntent();
    }
}