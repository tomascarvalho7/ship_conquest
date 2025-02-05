# Client Application Architecture
> **Ship Conquest** client mobile application made in *Flutter* with complex visuals.

## Why use *Flutter*?
> Flutter is one of the leading frameworks for building cross-platform mobile, web, 
> and desktop applications, known for its fast development, expressive UI, and native performance.

For the development of the client application the *Flutter Framework* was chosen for it's cross-platform technology, powerful documentation 
and ease developing complex UI we need.

Before arriving on *Flutter*, a couple of mobile frameworks were tested to develop a simple isometric moving scene. The best performance results were
arrived using the *Flutter Framework* with the use of different techniques that maximized performance. This techniques are also available in the previously
tested technologies, but personally were less intuitive to reach.

## Setup
> You can simply download on your mobile device the APK available on: *https://github.com/tomascarvalho7/ship_conquest/blob/main/flutter/apk* to run the application with everything setup.

### On a local machine
> To run the application on a local machine it is required to have installed the *Flutter SDK* and use a mobile emulator or a physical mobile device.

For the client application to run correctly with the *Google Services* it is required to include a keystore (*.jks*). This file is not included in the github repo for security reasons.
To request access to the keystore contact us.

Check if the Flutter SDK is installed correctly:
```shell
flutter doctor
```

The https://github.com/tomascarvalho7/ship_conquest/blob/main/flutter/lib/example_config.dart file contains two required fields missing. 
```flutter
const clientId = <Google client id>
const baseUri = <server application URI>
```
To request access to the *Google client id* contact us.

After filled, copy this file to the https://github.com/tomascarvalho7/ship_conquest/blob/main/flutter/lib/config.dart file where they will be accessed.


To run the mobile application do:
```shell
flutter run
```

To generate an application APK do:
```shell
flutter build apk --split-per-abi
```


## Client Application Design
> In this figure there are illustrated the main building layers of the application.

![application layers](https://github.com/tomascarvalho7/ship_conquest/blob/main/flutter/client_organization.png)

## Client Controllers Design
> In this figure is mapped the controllers organization and they're functionalities. 
> Each controller is a independent piece of business logic maintaining state.

![controllers organization](https://github.com/tomascarvalho7/ship_conquest/blob/main/flutter/controllers.png)



