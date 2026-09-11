# Sadoun Android Build Environment

JDK 17 | AGP 8.7.3 | Gradle 8.9 | compile/target SDK 35 | Build Tools 35.0.0.

Included:
- Gradle wrapper configuration
- Windows/Linux/Termux build helpers
- GitHub Actions workflow that provisions Android SDK and builds the debug APK

The current chat runtime does not contain Google's Android SDK/Build Tools or Gradle binaries, so an APK binary cannot be produced inside this session. Use Android Studio or the included GitHub Actions workflow.

The app code is still an MVP. Production guardian functions require further implementation and real-device testing.
