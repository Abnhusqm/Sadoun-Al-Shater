#!/data/data/com.termux/files/usr/bin/bash
set -e
echo "Termux build helper"
java -version
if command -v gradle >/dev/null; then gradle assembleDebug; else echo "Gradle is not installed; use Android Studio or cloud CI."; fi
