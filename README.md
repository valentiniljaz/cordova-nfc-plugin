# cordova-nfc-plugin
This Plugin reads the ID of NFC-Tags.

Usage:
----------------------------------------------------
####`nfc.addTechListener(function (win, fail));`

Adds a Listener for Tags with any Technology.

win-Function returns Tag-ID

fail-Function returns 
    "NO_NFC" if NFC is not supported or
    "NFC_DISABLED" if NFC is not enabled
	
####`NfcTech.removeTechListener(function (win, fail));`

win-Function returns "NFC_STOPPED"

fail-Function returns Exception message

####`NfcTech.isAvailable(function (win, fail));`

Checks if NFC is available.

win-Function returns "NFC_OK"

fail-Function returns 
    "NO_NFC" if NFC is not supported or
    "NFC_DISABLED" if NFC is not enabled