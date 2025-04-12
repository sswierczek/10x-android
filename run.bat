@echo off
echo Building and installing Android app...

REM Add Android SDK platform-tools to PATH
set "PATH=%PATH%;%LOCALAPPDATA%\Android\Sdk\platform-tools"

REM Set JAVA_HOME to Android Studio's bundled JDK
set "JAVA_HOME=C:\Program Files\Android\Android Studio\jbr"

REM Check if an Android device is connected
adb devices | find "device" > nul
if errorlevel 1 (
    echo No Android device found. Please connect a device or start an emulator.
    exit /b 1
)

REM Build and install the app
call .\gradlew.bat clean
if errorlevel 1 (
    echo Build failed
    exit /b 1
)

call .\gradlew.bat installDebug
if errorlevel 1 (
    echo Installation failed
    exit /b 1
)

echo App installed successfully!

REM Launch the main activity
adb shell am start -n com.example.a10xandroid/.MainActivity
if errorlevel 1 (
    echo Failed to launch activity
    exit /b 1
)

echo App launched successfully!