Smart Doctor Appointment App – Android Client

A modern Android application built using Kotlin and Jetpack Compose for the Smart Doctor Appointment System. 
The app provides appointment booking, doctor management, AI symptom checking, and role-based access for Patients, Doctors, and Admins.

Features
Patient, Doctor, and Admin Roles
JWT Authentication System
AI Symptom Checker
Appointment Booking & Scheduling
Doctor Dashboard for Appointment Management
Admin Approval Panel for Doctor Verification
Material Design 3 UI with Dark Mode Support
Retrofit API Integration with FastAPI Backend
Tech Stack
Kotlin
Jetpack Compose
Retrofit 2
Coroutines
Material Design 3
FastAPI Backend
JWT Authentication
Project Structure
android_app/
│
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/divyanshu/doctorapp/
│   │   │   ├── MainActivity.kt
│   │   │   ├── TokenManager.kt
│   │   │   ├── network/
│   │   │   └── ui/
│   │   └── res/
│   └── build.gradle.kts
│
├── gradlew
├── settings.gradle.kts
└── local.properties
Main Screens
Login Screen
Register Screen
Symptom Checker
Doctor List & Profile
Appointment Booking Screen
Patient Dashboard
Doctor Dashboard
Admin Panel
Setup
Install Dependencies

Open the project in Android Studio and allow Gradle to sync automatically.

Configure Backend URL

In RetrofitInstance.kt:

private const val BASE_URL = "http://10.0.2.2:8000/"

Replace with your local backend IP if using a physical device.

Run the App
Start the FastAPI backend server
Connect emulator or Android device
Press Run in Android Studio
Backend Communication

The app communicates with the FastAPI backend using Retrofit APIs and JWT authorization headers.

Authentication
Secure Login & Registration
JWT Token Storage
Automatic Authorization Header Injection
AI Symptom Checker

The app allows users to select symptoms and receive recommended doctor specializations from the backend AI engine.

Future Improvements
Push Notifications
Video Consultation
Firebase Integration
Payment Gateway
Cloud Database Support
Conclusion

This Android client demonstrates modern Android development practices using Jetpack Compose, Retrofit, and Kotlin while integrating seamlessly with the FastAPI backend for a complete healthcare appointment management solution.
