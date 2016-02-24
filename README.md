# cordova-nfc-plugin
This Plugin reads NfcV Tags.

Usage:
NfcTech.addTechListener(function (win, fail));
Adds a Listener for Tags with NfcV-Technology
win: 	
	returns 
		Tag-ID
fail:	
	returns 
		"NO_NFC" if NFC is not supported
		or
		"NFC_DISABLED" if NFC is not enabled 
		or
		Exception message


NfcTech.removeTechListener(function (win, fail));
win:
	returns
		"NFC_STOPPED"
fail:
	returns
		Exception message


NfcTech.isAvailable(function (win, fail));
Checks if NFC is available.
win: 
	returns
		"NFC_OK"
fail: 	
	returns 
		"NO_NFC" if NFC is not supported
		or
		"NFC_DISABLED" if NFC is not enabled