cordova.define("nfc.plugin.NfcVPlugin.nfc", function(require, exports, module) {
var NfcV = {
    init: function(success, error) {
        cordova.exec(success, error, "NfcVPlugin", "init", []);
    },
    checkNfcVAvailability: function(success, error) {
        cordova.exec(success, error, "NfcVPlugin", "checkNfcVAvailability", []);
    },
    startListening: function(success, error) {
        cordova.exec(success, error, "NfcVPlugin", "startListening", []);
    },
    stopListening: function(success, error) {
        cordova.exec(success, error, "NfcVPlugin", "stopListening", []);
    },
    readBlock: function(blockAddr, success, error) {
        cordova.exec(success, error, "NfcVPlugin", "readBlock", [blockAddr]);
    },
    writeBlock: function(blockAddr, blockData, success, error) {
        cordova.exec(success, error, "NfcVPlugin", "writeBlock", [blockAddr, blockData]);
    }
}
module.exports = NfcV;
});
