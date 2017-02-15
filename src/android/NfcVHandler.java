package nfc.plugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.apache.cordova.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.nio.ByteBuffer;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.*;
import android.nfc.Tag;
import android.widget.Toast;
import android.util.Log;

import org.json.JSONObject;
import java.util.*;

public class NfcVHandler {

	public static final String NFC_OK = "NFC_OK";
	public static final String NFC_INIT_OK = "NFC_INIT_OK";
	public static final String NFC_CHECK_OK = "NFC_CHECK_OK";
	private static final String NFC_INTENT_ACTIVE = "NFC_INTENT_ACTIVE";
	private static final String NFC_STOP = "NFC_STOP";
	private static final String E_NO_NFC = "E_NO_NFC";
	private static final String E_NFC_DISABLED = "E_NFC_DISABLED";
	private static final String E_NULL_TAG = "E_NULL_TAG";
	private static final String E_ADDR_TOO_LONG = "E_ADDR_TOO_LONG";

	private static final byte CMD_READ = (byte)0x20;
	private static final byte CMD_WRITE = (byte)0x21;
	private static final byte FLAGS_DATA_RATE = (byte)0x02;
	private static final byte FLAGS_DATA_RATE_AND_PROTOCOL_EXT = (byte)0x0A;

    private NfcAdapter nfcAdapter;
    private Activity activity;
	private CallbackContext callbackContext;

	private PendingIntent foregroundPendingIntent;
	private IntentFilter[] foregroundFilters;
	private Intent newIntent;

    public NfcVHandler(Activity activity) {
        this.activity = activity;

        this.getNfcAdapter();
        this.setupForegroundDispatch(this.activity);
    }

    private void getNfcAdapter() {
    	this.nfcAdapter = NfcAdapter.getDefaultAdapter(this.getActivity());
    }

    public void setCallbackContext(CallbackContext callbackContext) {
    	this.callbackContext = callbackContext;
    }

    public String checkNfcAdapter() {
		if (this.nfcAdapter == null) {
			return E_NO_NFC;
		} else if (!this.nfcAdapter.isEnabled()) {
			return E_NFC_DISABLED;
        } else {
			return NFC_OK;
        }
    }

    public void newIntent(Intent intent) {
        this.newIntent = intent;
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            this.callbackContext.success(NFC_INTENT_ACTIVE);
        }
    }

    /* Cordova methods */

    public void init() {
		this.callbackContext.success(NFC_INIT_OK);
	}

    public void checkNfcVAvailibility() {
		this.callbackContext.success(NFC_CHECK_OK);
	}

    public void startListening() {
    	String[][] foregroundTechList = new String[][]{
			new String [] { NfcV.class.getName() }
		};
    	this.nfcAdapter.enableForegroundDispatch(this.getActivity(), this.foregroundPendingIntent, this.foregroundFilters, foregroundTechList);
    }

    public void stopListening() {
    	this.stopForegroundDispatch(this.getActivity(), this.nfcAdapter);
    	this.callbackContext.success(NFC_STOP);
    }

    public void readBlock(JSONObject blockAddr) throws Exception {
    	byte[] readBlockAddr = this.argToBytes(blockAddr);

    	byte[] result = this.readNfcVBlock(readBlockAddr);

    	PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, result);
		this.callbackContext.sendPluginResult(pluginResult);
    }

    public void writeBlock(JSONObject blockAddr, JSONObject blockData) throws Exception {
    	byte[] writeBlockAddr = this.argToBytes(blockAddr);
    	byte[] writeBlockData = this.argToBytes(blockData);

    	byte[] result = this.writeNfcVBlock(writeBlockAddr, writeBlockData);

    	PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, result);
		this.callbackContext.sendPluginResult(pluginResult);
    }

    public void transceive(JSONObject jsonRequest) throws Exception {
    	byte[] request = this.argToBytes(jsonRequest);
    	byte[] result = this.transceiveNfcV(request);

    	PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, result);
		this.callbackContext.sendPluginResult(pluginResult);
    }

	/* Private methods */

	private byte[] transceiveNfcV(byte[] request) throws Exception {
		Tag tag = this.newIntent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		if (tag == null) {
			throw new Exception(E_NULL_TAG);
		}

		byte[] response = new byte[1];
		NfcV nfcv = NfcV.get(tag);
	
		if (nfcv != null) {
			try {
				nfcv.connect();
				response = nfcv.transceive(request);
			} catch (IOException e) {
				if (e.getMessage().equals("Tag was lost.")) {
                    // Continue, because of Tag bug
                } else {
                    throw e;
                }
			} finally {
				try {
					nfcv.close();
				} catch (IOException e) {}
			}
		}

		return response;
	}

	private byte[] readNfcVBlock(byte[] readBlock) throws Exception {
		// Prepare request
		byte[] request = new byte[2 + readBlock.length];
		request[0] = this.getRequestFlags(readBlock);
		request[1] = CMD_READ;
		for (int i = 0; i < readBlock.length; i++) {
			request[2 + i] = readBlock[i];
		}

		return this.transceiveNfcV(request);
	}

	private byte[] writeNfcVBlock(byte[] writeBlock, byte[] writeData) throws Exception {
		// Prepare request
		byte[] request = new byte[2 + writeBlock.length + writeData.length];
		request[0] = this.getRequestFlags(writeBlock);
		request[1] = CMD_WRITE;
		for (int i = 0; i < writeBlock.length; i++) {
			request[2 + i] = writeBlock[i];
		}
		for (int i = 0; i < writeData.length; i++) {
			request[2 + writeBlock.length + i] = writeData[i];
		}

        return this.transceiveNfcV(request);
	}

	private void setupForegroundDispatch(Activity activity) {
        Intent foregroundIntent = new Intent(activity.getApplicationContext(), activity.getClass());
        foregroundIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        this.foregroundPendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, foregroundIntent, 0);

        IntentFilter filter = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        try {
            filter.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("E_FILTER", e);
        }
        this.foregroundFilters = new IntentFilter[]{ filter };
    }

    private void stopForegroundDispatch(Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }

	private Activity getActivity() { 
		return this.activity; 
	}

    private Intent getIntent() {
        return this.activity.getIntent();
    }

    private byte getRequestFlags(byte[] blockAddr) throws Exception {
    	if (blockAddr.length == 1) {
    		return FLAGS_DATA_RATE;
    	} else if (blockAddr.length == 2) {
    		return FLAGS_DATA_RATE_AND_PROTOCOL_EXT;
    	} else {
    		throw new Exception(E_ADDR_TOO_LONG);
    	}
    }
	
    private static String bytesToHex(byte[] bytes){
        final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        char[] hexChars = new char[bytes.length * 2];
        int v;

        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j*2] = hexArray[v >>> 4];
            hexChars[j*2 + 1] = hexArray[v & 0x0F];
        }

        return new String(hexChars);
    }

    private byte[] argToBytes(JSONObject blockAddr) {
    	byte[] readBlock = new byte[1];

    	List<Byte> argsBytes = new ArrayList<Byte>();
    	Iterator<String> keys;
    	String key;
    	Byte[] readBytes;

    	try {
			keys = blockAddr.keys();
			while ( keys.hasNext() ) {
			    key = (String)keys.next();
			    argsBytes.add((byte)blockAddr.getInt(key));
			}

			readBytes = argsBytes.toArray(new Byte[argsBytes.size()]);
			readBlock = new byte[readBytes.length];
			for(int i = 0; i < readBytes.length; i++) {
				readBlock[i] = readBytes[i].byteValue();
			}
		} catch (Exception e) {
			callbackContext.error(e.getMessage());
		}

		return readBlock;
    }
}