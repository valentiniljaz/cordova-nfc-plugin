var NfcTech = {
	addTechListener: function (win, fail) {
        cordova.exec(win, fail, "NfcPlugin", "registerNdef", []);
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