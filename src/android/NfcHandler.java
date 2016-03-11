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

public class NfcHandler {
	private static final int block = 10;
    private static final String STATUS_NFC_OK = "NFC_OK";
    private static final String STATUS_NO_NFC = "NO_NFC";
    private static final String STATUS_NFC_DISABLED = "NFC_DISABLED";
    private static final String STATUS_NFC_STOPPED = "NFC_STOPPED";

    private NfcAdapter nfcAdapter;
    private Activity activity;
	private CallbackContext callbackContext;
    private boolean isReading, isWriting;
	private int message;

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
    public void startReadingNfcV(){
		nfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());
		if (nfcAdapter == null) {
			callbackContext.error(STATUS_NO_NFC);
		}else if (!nfcAdapter.isEnabled()){
			callbackContext.error(STATUS_NFC_DISABLED);
        }else {
			this.isReading = true;
			this.isWriting = false;
			setupForegroundDispatch(getActivity(), nfcAdapter);
        }
    }
    public void stopReadingNfcV(){
		try{
			this.isReading = false;
			stopForegroundDispatch(getActivity(), nfcAdapter);
			callbackContext.success(STATUS_NFC_STOPPED);
		}catch(IllegalStateException e){
			callbackContext.error(e.getMessage());
		}
    }
	public void startWritingNfcV(int message){
		nfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());
		if (nfcAdapter == null) {
			callbackContext.error(STATUS_NO_NFC);
		}else if (!nfcAdapter.isEnabled()){
			callbackContext.error(STATUS_NFC_DISABLED);
        }else {
			this.isWriting = true;
			this.isReading = false;
			this.message = message;
			setupForegroundDispatch(getActivity(), nfcAdapter);
        }
	}
	public void stopWritingNfcV(){
		try{
			this.isWriting = false;
			stopForegroundDispatch(getActivity(), nfcAdapter);
			callbackContext.success(STATUS_NFC_STOPPED);
		}catch(IllegalStateException e){
			callbackContext.error(e.getMessage());
		}
    }
    public void newIntent(Intent intent) {
        String action = intent.getAction();
        if ((this.isReading || this.isWriting) && NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
            handleNfcVIntent(intent);
        }
    }
	private void handleNfcVIntent(Intent intent) {
		Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		if (this.isReading){
			readNfcV(tag);
			this.isReading = false;
		}else if (this.isWriting){
			try{
				writeNfcV(tag, message);
			}catch(IOException e){
				callbackContext.error("IOException");
			}
			callbackContext.success("success");
		}
		
		//stopForegroundDispatch(getActivity(), nfcAdapter);
	}
	public void readNfcV (Tag tag) {
		if (tag == null) {
			callbackContext.error("NULL");
		}
		byte[] id = tag.getId();
		NfcV nfcv = NfcV.get(tag);
		if(nfcv != null){
			try {
				nfcv.connect();
				/*int offset = 0;  // offset of first block to read
				int blocks = 64;  // number of blocks to read
				byte[] cmd = new byte[]{
						(byte)0x60,                  // flags: addressed (= UID field present)
						(byte)0x23,                  // command: READ MULTIPLE BLOCKS
						(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,  // placeholder for tag UID
						(byte)(offset & 0x0ff),      // first block number
						(byte)((blocks - 1) & 0x0ff) // number of blocks (-1 as 0x00 means one block)
				};
				System.arraycopy(id, 0, cmd, 2, 8);
				id = nfcv.transceive(cmd);*/
				byte[] cmd = new byte[]{
						(byte)0x00,                  // flags: addressed (= UID field present)
						(byte)0x20,                  // command: READ ONE BLOCK
						(byte)block					 // IMMER im gleichen Block
				};
				id = nfcv.transceive(cmd);
			} catch (IOException e) {
				callbackContext.error(nfcv.toString());
			} finally {
				try {
					nfcv.close();
				} catch (IOException e) {
				}
			}
		}
		int value = byteArrayToInt(id);
		//String str = new String(id);
		/*String result = "";
		try{
			result = (str.split("eqx")[1]).split("#")[0];
			if ("".equals(result)){
				result = "null";
			}
		}catch(Exception e){
			result = "null";
		}*/
		PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, value);
		callbackContext.sendPluginResult(pluginResult);
	}
	public static int byteArrayToInt(byte[] b) {
		if (b.length == 4)
		  return b[0] << 24 | (b[1] & 0xff) << 16 | (b[2] & 0xff) << 8
			  | (b[3] & 0xff);
		else if (b.length == 2)
		  return 0x00 << 24 | 0x00 << 16 | (b[0] & 0xff) << 8 | (b[1] & 0xff);

		return 0;
	}
	public static byte[] intToByteArray(int value) {
		return new byte[] {
				(byte)(value >>> 24),
				(byte)(value >>> 16),
				(byte)(value >>> 8),
				(byte)value};
	}
	public void writeNfcV(Tag tag, int id) throws IOException{
		//String write = "eqx" + id + "#";
		//byte[] data = write.getBytes(StandardCharsets.UTF_8);
		byte[] data = ByteBuffer.allocate(4).putInt(id).array();
		
		Toast.makeText(getActivity(), "given id is: " + data[0] + ";" + data[1] + ";" + data[2] + ";" + data[3],
		Toast.LENGTH_LONG).show();
		if (tag == null) {
			callbackContext.error("NULL");
			return;
		}
		NfcV nfcv = NfcV.get(tag);

		nfcv.connect();
/*
		// NfcV Tag has 64 Blocks with 4 Byte
		if ((data.length / 4) > 64) {
			// ERROR HERE!
			callbackContext.error("too long");
		}

		if ((data.length % 4) != 0) {
			byte[] ndata = new byte[(data.length) + (4 - (data.length % 4))];
			Arrays.fill(ndata, (byte) 0x00);
			System.arraycopy(data, 0, ndata, 0, data.length);
			data = ndata;
		}
*/
		byte[] arrByte = new byte[7];
		// Flags
		arrByte[0] = 0x00;
		// Command
		arrByte[1] = 0x21;
		arrByte[2] = (byte) block; // IMMER im gleichen Block speichern
		arrByte[3] = (byte) data[0];
		arrByte[4] = (byte) data[1];
		arrByte[5] = (byte) data[2];
		arrByte[6] = (byte) data[3];
		/*
		for (int i = 0; i < (data.length / 4); i++) {

			// block number
			arrByte[2] = (byte) (i);

			// data, DONT SEND LSB FIRST!
			arrByte[3] = data[(i * 4)];
			arrByte[4] = data[(i * 4) + 1];
			arrByte[5] = data[(i * 4) + 2];
			arrByte[6] = data[(i * 4) + 3];

			try {
			nfcv.transceive(arrByte);
			} catch (IOException e) {
			if (e.getMessage().equals("Tag was lost.")) {
				// continue, because of Tag bug
			} else {
				callbackContext.error("Couldn't write on Tag");
				throw e;
			}
			}
		}*/
		try {
			nfcv.transceive(arrByte);
			byte[] cmd = new byte[]{
						(byte)0x00,                  // flags: addressed (= UID field present)
						(byte)0x20,                  // command: READ ONE BLOCK
						(byte)block					 // IMMER im gleichen Block
				};
			byte[] result = nfcv.transceive(cmd);
			Toast.makeText(getActivity(), "read id is: " + result[0] + ";" + result[1] + ";" + result[2] + ";" + result[3],
			Toast.LENGTH_LONG).show();
			} catch (IOException e) {
			if (e.getMessage().equals("Tag was lost.")) {
				// continue, because of Tag bug
			} else {
				callbackContext.error("Couldn't write on Tag");
				throw e;
			}
			}
		nfcv.close();
	}
	
    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter filter = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        String[][] techList = new String[][]{
			new String [] {NfcV.class.getName()}
			};
        try {
            filter.addDataType("*/*");
        }catch (IntentFilter.MalformedMimeTypeException e){
            throw new RuntimeException("ERROR", e);
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
    private Activity getActivity() { return this.activity; }

    private Intent getIntent() {
        return this.activity.getIntent();
    }
}