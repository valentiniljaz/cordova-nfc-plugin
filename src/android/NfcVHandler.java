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
/*
https://books.google.at/books?id=c4QRU17e494C&pg=SA2-PA147&lpg=SA2-PA147&dq=read+and+write+nfcv&source=bl&ots=V9C-4JZR8N&sig=gjhjDZFGjewd05RQNsp-6zhZFvI&hl=de&sa=X&ved=0ahUKEwjDhYzJ1rnLAhWCVywKHVt3AaUQ6AEIZjAJ#v=onepage&q=read%20and%20write%20nfcv&f=false
*/
public class NfcVHandler {
	private static final int block = 1;
	
    private static final String STATUS_NFC_OK = "NFC_OK";
    private static final String STATUS_NO_NFC = "NO_NFC";
    private static final String STATUS_NFC_DISABLED = "NFC_DISABLED";
    private static final String STATUS_READING_STOPPED = "READING_STOPPED";
	private static final String STATUS_WRITING_STOPPED = "WRITING_STOPPED";

    private NfcAdapter nfcAdapter;
    private Activity activity;
	private CallbackContext callbackContext;
    private boolean isReading, isWriting;
	private int message;

    public NfcVHandler(Activity activity, final CallbackContext callbackContext){
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
			callbackContext.success(STATUS_READING_STOPPED);
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
			callbackContext.success(STATUS_WRITING_STOPPED);
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
			this.isWriting = false;
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
				byte[] cmd = new byte[]{
						(byte)0x00,                  // flag
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
		// erster block = flag
		byte[] result = new byte[]{ id[1], id[2], id[3], id[4] };
		int resultInt = ByteBuffer.wrap(result).order(java.nio.ByteOrder.BIG_ENDIAN).getInt();
		PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, resultInt);
		callbackContext.sendPluginResult(pluginResult);
	}
	public void writeNfcV(Tag tag, int id) throws IOException {
		byte[] data = ByteBuffer.allocate(4).putInt(id).array();
		if (tag == null) {
			callbackContext.error("NULL");
			return;
		}
		NfcV nfcv = NfcV.get(tag);

		nfcv.connect();
		
		byte[] arrByte = new byte[7];
		arrByte[0] = 0x00;			// flag
		arrByte[1] = 0x21;			// command: WRITE ONE BLOCK
		arrByte[2] = (byte) block; 	// IMMER im gleichen Block speichern
		
		arrByte[3] = (byte) data[0];
		arrByte[4] = (byte) data[1];
		arrByte[5] = (byte) data[2];
		arrByte[6] = (byte) data[3];
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
		nfcv.close();
		readNfcV(tag);
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