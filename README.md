# native-android-sample-app

## Overview

This repo provides a working example of integrating Verisoul's WebView into an Android
native app

The app opens the hidden webview and loads the Verisoul webview. The app then listens for a
secure `session_id` from the webview and makes an API call to the Verisoul API to retrieve the account prediction. _The
API call is included as an example but in production this should be done on your backend._

To run the app a Verisoul API Key is required. Schedule a call [here](https://meetings.hubspot.com/henry-legard) to get started.

## Quickstart

1. Download and install the latest [Android Studio](https://developer.android.com/studio)

2. Clone this repo

```console
git clone https://github.com/verisoul/native-android-sample-app.git
```

3. Open the project in Android Studio and update the following variables
- Update `projectId` with your project id in the `MainActivity.kt` file
- Update `apiKey` with your api key in the `MainActivity.kt` file

5. Select the simulator or plug in your Android device and click Run
