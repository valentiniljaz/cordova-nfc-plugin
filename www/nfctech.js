var nfctech = {
	addNdefListener: function (callback, win, fail) {
        document.addEventListener("nfctech", callback, false);
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
module.exports = nfctech;