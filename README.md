# cordova-nfc-plugin
This Plugin reads and writes data of NfcV tags.

Usage:
----------------------------------------------------
####`nfc.addNfcVListener: function (success, error));`

Adds a Listener for NfcV-Tags

success-Function returns Integer Data of block 50 (4 byte)

error-Function returns 
    "NO_NFC" if NFC is not supported or
    "NFC_DISABLED" if NFC is not enabled
	
####`nfc.removeNfcVListener: function (success, error));`

success-Function returns "READING_STOPPED"

error-Function returns Exception message

####`nfc.addNfcVListener: function (id, success, error));`

Writes int-id in the block 50 and returns it.

success-Function returns Integer Data of block 50 (4 byte)

error-Function returns 
    "NO_NFC" if NFC is not supported or
    "NFC_DISABLED" if NFC is not enabled

####`nfc.removeNfcVWriter: function (success, error));`

success-Function returns "WRITING_STOPPED"

error-Function returns Exception message

####`NfcTech.isAvailable: function (success, error));`

Checks if NFC is available.

success-Function returns "NFC_OK"

error-Function returns 
    "NO_NFC" if NFC is not supported or
    "NFC_DISABLED" if NFC is not enabled