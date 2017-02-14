# cordova-nfc-plugin

Plugin reads and writes data of NfcV tags.

Usage:
----------------------------------------------------

####`NfcV.init: function (success, error));`

Initialize plugin

success - Function returns "NFC_INIT_OK"

error - Check error flags below


####`NfcV.checkNfcVAvailability: function (success, error));`

Check if Nfc hardware available

success - Function returns "NFC_CHECK_OK"

error - Check error flags below


####`NfcV.startListening: function (success, error));`

Starts listening for new "ACTION_TECH_DISCOVERED" intent.

success - When intent recieved it returns "NFC_INTENT_ACTIVE"

error - Check error flags below


####`NfcV.startListening: function (success, error));`

Starts listening for new "ACTION_TECH_DISCOVERED" intent.

success - When intent is recieved, it returns "NFC_INTENT_ACTIVE"

error - Check error flags below


####`NfcV.stopListening: function (success, error));`

It disables foreground dispatch. Intent are no longer received.

success - It returns "NFC_STOP"

error - Check error flags below


####`NfcV.readBlock: function (blockAddr, success, error));`

Reads one block from `blockAddr`.

success - It returns bytes read from block at `blockAddr`

error - Check error flags below


####`NfcV.writeBlock: function (blockAddr, blockData, success, error));`

Writes `blockData` into one block at `blockAddr`.

success - It returns bytes from response

error - Check error flags below


####ERRORs

"E_NO_NFC" - NFC is not supported
"E_NFC_DISABLED" - NFC is not enabled
"E_NULL_TAG" - Tag returned NULL
"E_ADDR_TOO_LONG" - Block addr is too long (more than 2 bytes)