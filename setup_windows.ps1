$ErrorActionPreference="Stop"
java -version
if (-not $env:ANDROID_HOME -and -not $env:ANDROID_SDK_ROOT) { throw "Set ANDROID_HOME or ANDROID_SDK_ROOT." }
gradle --version
gradle assembleDebug
Write-Host "APK: app\build\outputs\apk\debug\app-debug.apk"
