# AndroidApps
Some Android Demo apps

1. Android Basics - Dummy application with set of basic widgets and layouts.
2. Birthday App - Simple application with Birthday greeting.
3. Earth Quake Reporter - Application reporting about earthquakes in the area specified by zip.
4. Map One - Application showing map and location services features of android sdk.
6. Miwok - Dummy translator from Native American language Miwok to English.
7. Miwok2 - Miwok applicaiton with extended functionality.
8. Pets - Dummy Pet catalogue.
9. Sunshine - Just another weather applicaiton.

## Build and run

- Build
```
./gradlew clean build
```

- Copy built apk to the device.

- Install apk

# Notes

- Google Services debug SHA-1 command
```
  keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android

```

- Earth Quake reporter requires internet access for getting data.
