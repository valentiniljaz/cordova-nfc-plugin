# cordova-nfc-plugin
This Plugin reads NfcV Tags.

Usage:
----------------------------------------------------
####`NfcTech.addTechListener(function (win, fail));`

Adds a Listener for Tags with NfcV-Technology.

win-Function returns Tag-ID

fail-Function returns 
    "NO_NFC" if NFC is not supported or
    "NFC_DISABLED" if NFC is not enabled or
    Exception message
####`NfcTech.removeTechListener(function (win, fail));`

win-Function returns "NFC_STOPPED"

fail-Function returns Exception message

####`NfcTech.isAvailable(function (win, fail));`

Checks if NFC is available.

win-Function returns "NFC_OK"

fail-Function returns 
    "NO_NFC" if NFC is not supported or
    "NFC_DISABLED" if NFC is not enabled