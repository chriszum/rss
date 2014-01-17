SimpleRss
===

News Aggregator for Android 4.4+.

While I do not recommend using the 4.0+ version in the apk folder, it is there just in case anyone really wants to try it. I plan to release 4.0+ versions at significant milestones. The features of java 7 keep the source code a lot neater.

Permissions:
```
   <uses-permission android:name="android.permission.INTERNET"/>
```
INTERNET - This is to download rss feeds and images from the feeds.
   
```
   <uses-permission android:name="android.permission.WAKE_LOCK"/>
```
WAKE_LOCK - This is to update feeds when the app is closed mid update and when scheduled to update (in settings).

You can find the latest signed APK in the apk folder.

Building
===

Make sure to have the Android SDK Platform API 19 installed.

To build the code using Gradle:
```
   ./gradlew assembleDebug
```

To Install the apk to a device connected with adb:
```
   adb install -r build/apk/SimpleRss-debug-unaligned.apk
```

Bugs
===

If you experience any crashes, connect to usb, enable USB Debugging in
Androids Settings:Developer options, and use:
```
   adb logcat
```

If you do not have Developer options, rapidly tap
Settings:About phone:Build number until you are one.

This will print out lots of text to the terminal. Reproduce the crash
while running this and include the log in a git issue with instructions
on how to reproduce and which device you are using.
