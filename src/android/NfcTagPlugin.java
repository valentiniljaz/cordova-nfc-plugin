package nfc.plugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.PluginResult;
import org.apache.cordova.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import nfc.plugin.NfcHandler;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;

public class NfcTagPlugin extends CordovaPlugin {
    private static final String START_READING_NFCV = "startReadingNfcV";
    private static final String STOP_READING_NFCV = "stopReadingNfcV";
	private static final String START_WRITING_NFCV = "startWritingNfcV";
    private static final String STOP_WRITING_NFCV = "stopWritingNfcV";
    private static final String CHECK_NFC_AVAILIBILITY = "checkNfc";
	
	private NfcHandler handler;
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		handler = new NfcHandler(this.cordova.getActivity(), callbackContext);
		if(action.equalsIgnoreCase(START_READING_NFCV)){
            handler.startReadingNfcV();
        } else if(action.equalsIgnoreCase(STOP_READING_NFCV)){
            handler.stopReadingNfcV();
        } else if(action.equalsIgnoreCase(CHECK_NFC_AVAILIBILITY)){
			handler.checkNfcAvailibility();
		} else if(action.equalsIgnoreCase(START_WRITING_NFCV)){
			handler.startWritingNfcV(args.getInt(0));
		} else if(action.equalsIgnoreCase(STOP_WRITING_NFCV)){
			handler.stopWritingNfcV();
		}else {
            // invalid action
            return false;
        }
        return true;
    }
	
	@Override
    public void onNewIntent(Intent intent) {
		handler.newIntent(intent);
    }
}