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

public class NfcTechPlugin extends CordovaPlugin {

    private NfcAdapter nfcAdapter;
	private CallbackContext callbackContext;
	
    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;
		NfcHandler handler = new NfcHandler();
		if("readNfcTech".equals(action)){
            return handler.startReadingNfc(callbackContext);
        }
		if("checkNfc".equals(action)){
			return handler.checkNfcAvailibility(callbackContext);
		}
        return false;
    }
}