# SafeWordSOS 🚨

SafeWordSOS is an Android-based emergency response and personal safety system designed to help users request assistance when they may not be able to safely operate their phone.

The application allows users to configure a personal emergency safe word and trusted contact. When the safe word is detected or a shake-based emergency gesture is triggered, SafeWordSOS starts an SOS countdown. If the emergency is confirmed, the application shares the user's GPS location with the trusted contact and starts recording audio evidence.

## Key Features

- 🎙️ Voice-based Safe Word detection
- 📳 Shake-based SOS activation
- ⏱️ SOS countdown with cancellation option
- 📍 Real-time GPS location sharing
- 📱 Emergency SMS to trusted contacts
- 🎤 Emergency audio recording
- 🔄 Background monitoring using Android Foreground Services
- 🔐 User-configurable safe word and trusted contact
- 🚨 Minimal phone interaction during emergencies

## Technology Stack

- Java
- Android Studio
- Android SDK
- SpeechRecognizer
- Foreground Services
- GPS / Location Services
- SMS Manager
- MediaRecorder
- Android Sensors / Accelerometer

## Project Goal

The goal of SafeWordSOS is to make emergency assistance faster and easier to trigger, especially in situations where manually opening an app, unlocking a phone, or navigating through multiple screens may not be practical.

## Future Scope

- Smartwatch and wearable integration
- Intelligent distress detection
- Improved false-trigger prevention
- Emergency contact networks
- Safety analytics and risk detection
- Offline emergency capabilities
