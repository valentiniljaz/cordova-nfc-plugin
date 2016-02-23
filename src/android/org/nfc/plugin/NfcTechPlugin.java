package org.nfc.plugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.PluginResult;
import org.apache.cordova.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.nfc.plugin.NfcHandler;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;

public class NfcTechPlugin extends CordovaPlugin {
	
	private NfcHandler handler;
    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
		handler = new NfcHandler(return this.cordova.getActivity(), callbackContext);
		if("readNfcTech".equals(action)){
            return handler.startReadingNfc();
        }
		if("checkNfc".equals(action)){
			return handler.checkNfcAvailibility();
		}
        return false;
    }
	
	@Override
    public void onNewIntent(Intent intent) {
		handler.newIntent(intent);
    }
}