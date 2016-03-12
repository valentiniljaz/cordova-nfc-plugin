var nfc = {
	addNfcVListener: function (success, error) {
        cordova.exec(success, error, "NfcTagPlugin", "startReadingNfcV", []);
    },
	removeNfcVListener: function (success, error) {
        cordova.exec(success, error, "NfcTagPlugin", "stopReadingNfcV", []);
    },
	addNfcVWriter: function (id, success, error) {
        cordova.exec(success, error, "NfcTagPlugin", "startWritingNfcV", [id]);
    },
	removeNfcVWriter: function (success, error) {
        cordova.exec(success, error, "NfcTagPlugin", "stopWritingNfcV", []);
    },
	isAvailable: function (success, error) {
        cordova.exec(success, error, "NfcTagPlugin", "checkNfc", []);
    }
}
module.exports = nfc;