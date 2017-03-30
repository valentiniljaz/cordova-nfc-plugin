# Angular NfcV Service

Service provides helpers methods for accessing NfcV tags on Android

## Install

```npm install https://github.com/valentiniljaz/cordova-nfc-plugin```

Add provider for NfcvService within your module:

```
import {NfcvService} from 'cordova-nfc-plugin/ionic2';

@NgModule({
  providers: [
    NfcvService
  ]
})
export class MyModule {}

```

## NfcvService.addNdefListener

In order to get notified whenever a new tag is discovered you need to setup two things:

1) Within `platform.ready()` setup ndefListener: 
```
nfcvService.addNdefListener();
```
This method will add new event listener for Ndef messages. Whenever a new tag is discovered it will push new event down the observable stream. You can subscribe to this stream and you'll be notified whenever a tag is discovered.

2) Subscribe to obsevable:
```
this.nfcvService.onTag().subscribe((tag) => {
    if (tag !== null) {
        console.log('Tag', tag);
    }
});
```
Since this is BehaviorSubject the first tag will always be `null`. You can subscribe within `ngOnInit` method or basically any other as well.

`addNdefListener` method starts listening for Ndef messages. Whenever it receives a Ndef message it dispatches an event. You can listen to the event by subscribing with `onTag` method. Cordova plugin sends Ndef message as byte array so you need to parse the array in order to retrieve Ndef message. Method `parseNdef` is used to parse Ndef messages. At the moment `parseNdef` only understands messages advertised by Nfcv hardware refered to in attached datasheet.

Complete example:
```
import {Component, OnInit} from '@angular/core';
import {Platform} from 'ionic-angular';
import {StatusBar, Splashscreen, Device} from 'ionic-native';

import {NfcvService} from 'cordova-nfc-plugin/ionic2';

@Component({
    templateUrl: 'my-app.html'
})
export class MyApp implements OnInit {
    constructor(    platform: Platform,
                    public nfcvService: NfcvService)
    {
        platform.ready().then(() => {
            StatusBar.styleDefault();
            Splashscreen.hide();

            nfcvService.addNdefListener();
        });
    }

    ngOnInit() {
        this.nfcvService.onTag().subscribe((tag) => {
            console.log('Tag', tag);
        });
    }
}

```

## NfcvService.read

`read` method accepts array of blocks from which to read data. It returns new array with block addresses and block data.

```
this.nfcvService.read([
    { block: new Uint8Array([0x01]) },
    { block: new Uint8Array([0x02]) }
], true, device)
    .then((data) => {
        console.log(data);
    });
```

`device` object represents the device with which you wish to communicate, it is an optional argument. At the moment `device` must only provide one key 
`regexp`, which is used to match the device Ndef message read by Nfc adapter.

```
var device = {"regexp": new RegExp('<regula-exp>', 'i')};
```

## NfcvService.write

Method writes data to specified block addresses.

```
this.nfcvService.write([
    { block: new Uint8Array([0x01]]), data: new Uint8Array([0x01, 0x02, 0x03, 0x04]) },
    { block: new Uint8Array([0x02]]), data: new Uint8Array([0x01, 0x02, 0x03, 0x04]) }
], true, device);
```

## NfcvService.init

`init` method is not required in most cases. Everytime you invoke any other method (addNdefListener, read, write etc.) from Cordova plugin, NfcV adapater is initialized. But sometimes you would like to initilize the adpater to see if the NfcV adapter can be properly setup, in this case you can invoke `init`.

```
import { Component } from '@angular/core';
import { Platform } from 'ionic-angular';
import { StatusBar } from '@ionic-native/status-bar';
import { SplashScreen } from '@ionic-native/splash-screen';

import {NfcvService} from 'cordova-nfc-plugin/ionic2';

@Component({
  templateUrl: 'my-app.html'
})
export class MyApp {
  constructor(platform: Platform, statusBar: StatusBar, splashScreen: SplashScreen, nfcvService: NfcvService) {
    platform.ready().then(() => {
      statusBar.styleDefault();
      splashScreen.hide();

      nfcvService.init().then((success) => {
        console.log('NfcV OK', success);
      });
    });
  }
}
```

## NfcvService.isAvailable

Simillary to `init` you can use `isAvailable` method to check if NfcV adapter within a smartphone is actually available.

```
nfcvService.isAvailable().then((success) => {
  console.log('NfcV Available', success);
});
```
