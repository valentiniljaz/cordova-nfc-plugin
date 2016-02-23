var NfcTech = {
	addTechListener: function (win, fail) {
        cordova.exec(win, fail, "NfcTechPlugin", "startReadingNfcTech", []);
    },
	removeTechListener: function (win, fail) {
        cordova.exec(win, fail, "NfcTechPlugin", "stopReadingNfcTech", []);
    },
	isAvailable: function (win, fail) {
        cordova.exec(win, fail, "NfcTechPlugin", "checkNfc", []);
    }
}
module.exports = NfcTech;