AAOS Car Properties Viewer (Kotlin)
===========================================
Demonstrates connecting to the Car API and listening for several events. Works on
Android P (API 28) and above.

Introduction
============
This app allows a user to see the current gear, ignition state, gear selection and parking break
on the main infotainment screen (IVI). It also shows a list of all properties available in the car.

The app connects to the Car API and subscribes to several events using `CarPropertyManager`.
If it receives a change event, it updates the UI; if it receives an error event, it just logs them
with logcat.

Please find Car API documentation on https://developer.android.com/reference/android/car/packages
and car services source code at
https://cs.android.com/android/platform/superproject/+/master:packages/services/Car/car-lib

Prerequisites
--------------

- [Android 10 SDK (API level 29) Revision 5](https://developer.android.com/studio/releases/platforms#10) or newer.
- A device running Android Automotive OS P or newer.

Getting Started
---------------
This sample uses the Gradle build system. To build this project, use the
"gradlew build" command or use "Import Project" in Android Studio.

Support
-------

- Stack Overflow: https://stackoverflow.com/questions/tagged/android-automotive
