var nfc = {
	addTagListener: function (success, error) {
        cordova.exec(success, error, "NfcTagPlugin", "startReadingNfcV", []);
    },
	removeTagListener: function (success, error) {
        cordova.exec(success, error, "NfcTagPlugin", "stopReadingNfcV", []);
    },
	addTagWriter: function (success, error) {
        cordova.exec(success, error, "NfcTagPlugin", "startWritingNfcV", []);
    },
	removeTagWriter: function (id, success, error) {
        cordova.exec(success, error, "NfcTagPlugin", "stopWritingNfcV", [id]);
    },
	isAvailable: function (success, error) {
        cordova.exec(success, error, "NfcTagPlugin", "checkNfc", []);
    }
}
module.exports = nfc;