var nfc = {
	addTagListener: function (success, error) {
        cordova.exec(success, error, "NfcTagPlugin", "startReadingNfcTags", []);
    },
	removeTagListener: function (success, error) {
        cordova.exec(success, error, "NfcTagPlugin", "stopReadingNfcTags", []);
    },
	isAvailable: function (success, error) {
        cordova.exec(success, error, "NfcTagPlugin", "checkNfc", []);
    }
}
module.exports = nfc;