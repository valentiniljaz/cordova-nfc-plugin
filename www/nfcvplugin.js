var nfc = {
	addNfcVListener: function (success, error) {
        cordova.exec(success, error, "NfcVPlugin", "startReadingNfcV", []);
    },
	removeNfcVListener: function (success, error) {
        cordova.exec(success, error, "NfcVPlugin", "stopReadingNfcV", []);
    },
	addNfcVWriter: function (id, success, error) {
        cordova.exec(success, error, "NfcVPlugin", "startWritingNfcV", [id]);
    },
	removeNfcVWriter: function (success, error) {
        cordova.exec(success, error, "NfcVPlugin", "stopWritingNfcV", []);
    },
	isAvailable: function (success, error) {
        cordova.exec(success, error, "NfcVPlugin", "checkNfc", []);
    }
}
module.exports = nfc;