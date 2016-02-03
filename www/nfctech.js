var NfcTech = {
	addTechListener: function (win, fail) {
        cordova.exec(win, fail, "NfcTechPlugin", "readNfcTech", []);
    },
	isAvailable: function (win, fail) {
        cordova.exec(win, fail, "NfcTechPlugin", "checkNfc", []);
    }
}
module.exports = NfcTech;