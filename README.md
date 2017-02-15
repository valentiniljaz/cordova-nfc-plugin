# cordova-nfc-plugin

Plugin reads and writes data of NfcV tags.

Usage:
----------------------------------------------------

####`NfcV.init: function (success, error));`

Initialize plugin

* success - Function returns "NFC_INIT_OK"
* error - Check error flags below


####`NfcV.checkNfcVAvailability: function (success, error));`

Check if Nfc hardware available

* success - Function returns "NFC_CHECK_OK"
* error - Check error flags below


####`NfcV.startListening: function (success, error));`

Starts listening for new "ACTION_TECH_DISCOVERED" intent.

* success - When intent recieved it returns "NFC_INTENT_ACTIVE"
* error - Check error flags below


####`NfcV.stopListening: function (success, error));`

It disables foreground dispatch. Intent are no longer received.

* success - It returns "NFC_STOP"
* error - Check error flags below

####`NfcV.transceive: function (request, success, error));`

It is used to dispatch any kind of request against a NFC tag. Request object has to include a full request: flags, block_addr and any data.

* success - It returns response from the request. If it is a read request it returns the read data. If it is a write request it returns write response.
* error - Check error flags below


####`NfcV.readBlock: function (blockAddr, success, error));`

Reads one block from `blockAddr`.

* success - It returns bytes read from block at `blockAddr` along with response flags
* error - Check error flags below


####`NfcV.writeBlock: function (blockAddr, blockData, success, error));`

Writes `blockData` into one block at `blockAddr`.

* success - It returns bytes from write response (error flag and any error code)
* error - Check error flags below


####ERRORs

* `E_NO_NFC` - NFC is not supported
* `E_NFC_DISABLED` - NFC is not enabled
* `E_NULL_TAG` - Tag returned NULL
* `E_ADDR_TOO_LONG` - Block addr is too long (more than 2 bytes)

####Datasheet

Refer to attached datasheet for futher clarifications (chapters: 19, 20, 26).