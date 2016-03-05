var NfcTech = {
	addTechListener: function (success, error) {
        cordova.exec(success, error, "NfcTechPlugin", "startReadingNfcTech", []);
    },
	removeTechListener: function (success, error) {
        cordova.exec(success, error, "NfcTechPlugin", "stopReadingNfcTech", []);
    },
	isAvailable: function (success, error) {
        cordova.exec(success, error, "NfcTechPlugin", "checkNfc", []);
    }
}
module.exports = NfcTech;