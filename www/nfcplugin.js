var nfc = {
	addTagListener: function (success, error) {
        cordova.exec(success, error, "NfcTagPlugin", "startReadingNfcV", []);
    },
	removeTagListener: function (success, error) {
        cordova.exec(success, error, "NfcTagPlugin", "stopReadingNfcV", []);
    },
	addTagWriter: function (id, success, error) {
        cordova.exec(success, error, "NfcTagPlugin", "startWritingNfcV", [id]);
    },
	removeTagWriter: function (success, error) {
        cordova.exec(success, error, "NfcTagPlugin", "stopWritingNfcV", []);
    },
	isAvailable: function (success, error) {
        cordova.exec(success, error, "NfcTagPlugin", "checkNfc", []);
    }
}
module.exports = nfc;