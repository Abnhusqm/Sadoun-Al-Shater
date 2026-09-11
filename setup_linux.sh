#!/usr/bin/env bash
set -e
java -version
test -n "$ANDROID_HOME" -o -n "$ANDROID_SDK_ROOT" || { echo "Set ANDROID_HOME/ANDROID_SDK_ROOT"; exit 1; }
gradle --version
gradle assembleDebug
echo "APK: app/build/outputs/apk/debug/app-debug.apk"
