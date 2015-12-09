var NfcTech = {
	addTechListener: function (win, fail) {
        cordova.exec(win, fail, "NfcTechPlugin", "readNfcTech", []);
    }
	/*createEvent: function(successCallback, errorCallback){
		cordova.exec(
			successCallback,
			errorCallback,
			'NfcTechPlugin'
			'readNfcTech'
			[]
		);
	}*/
}
module.exports = NfcTech;