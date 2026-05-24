# Face Recognition Attendance (FRA)

FRA is a sophisticated Android application designed for streamlined attendance management using advanced Face Recognition and dynamic QR codes. Built natively with Kotlin and Jetpack Compose, it features a modern, cyber-neon UI.

## Features
- **Teacher Dashboard**: Create sessions, manage students, view detailed attendance records, and export data directly to Excel (.xlsx).
- **Student Dashboard**: Quick face registration, instant attendance marking via QR code scanning or live face verification.
- **Offline First**: All face embeddings and attendance records are stored securely on the device using a local Room Database.
- **Continuous Real-Time Scanning**: Advanced pipeline to continually process camera frames and match facial embeddings on the fly without manual button presses.

## Installation & Setup

### Prerequisites
- [Android Studio (Giraffe or newer recommended)](https://developer.android.com/studio)
- JDK 17
- Android SDK (API 34)

### How to Clone and Build
1. **Clone the repository** to your local machine:
   ```bash
   git clone https://github.com/Fearcine/fra.git
   ```
2. **Open the project** in Android Studio:
   - Launch Android Studio.
   - Click on **File > Open**.
   - Navigate to the cloned `fra` folder and select it.
3. **Sync Gradle**:
   - Wait for Android Studio to automatically sync the Gradle project.
   - If it doesn't, click the "Sync Project with Gradle Files" button in the toolbar.
4. **Run the App**:
   - Connect your Android device via USB (ensure Developer Options and USB Debugging are enabled) or start an Android Emulator.
   - Click the green **Run (Play)** button in Android Studio, or press `Shift + F10`.

## Tech Stack
- Kotlin
- Jetpack Compose (Material 3)
- Room Database
- CameraX
- ML Kit (Face Detection)
- MobileFaceNet (TFLite)
- Apache POI (Excel Export)
