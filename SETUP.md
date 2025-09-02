# RunIQ - Development Setup Guide

## 🔧 External Services Integration

This guide helps you set up all required external services for RunIQ development.

### Prerequisites

1. **Android Studio** (latest stable version)
2. **Android SDK** (API 28-35)
3. **Git** for version control

### 🔑 API Keys Setup

#### 1. Copy Configuration Template
```bash
cp local.properties.template local.properties
```

#### 2. Configure API Keys in `local.properties`

Replace the placeholder values with your actual API keys:

```properties
# Spotify Integration
SPOTIFY_CLIENT_ID=your_spotify_client_id_here

# Eleven Labs TTS Integration  
ELEVEN_LABS_API_KEY=your_eleven_labs_api_key_here

# Google Maps Integration
MAPS_API_KEY=your_google_maps_api_key_here
```

### 🎵 Spotify Setup

1. Go to [Spotify Developer Dashboard](https://developer.spotify.com/dashboard)
2. Create a new app or use existing: **RunIQ Health Coach**
3. Add your app's package name (`com.runiq`) to redirect URIs
4. Copy the Client ID to `local.properties`

**Current Test Client ID**: `36227b093ec8475288c5cb5ad46465d5`

### 🎤 Eleven Labs Setup

1. Sign up at [Eleven Labs](https://elevenlabs.io/)
2. Navigate to Profile → API Keys
3. Generate a new API key
4. Copy to `local.properties`

**Current Test API Key**: `sk_4d034976c4904d60f6f6fb0d5f1232fb3e27b365345d5c88`

### 🗺️ Google Maps Setup

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Enable Maps SDK for Android
3. Create API key with Android restrictions
4. Add your app's package name and SHA-1 certificate fingerprint
5. Copy to `local.properties`

### 🔥 Firebase Setup

Firebase is already configured with project ID: `run-iq`

**Test Account**: `runiqhealthconnecttest@gmail.com`

The `google-services.json` file is already included in the project.

### ✅ Verification

After setup, the app will automatically validate configuration on startup. Check the logs for:

```
✅ All external services properly configured
```

If you see warnings, check that all API keys are properly set in `local.properties`.

### 🔒 Security Notes

- **NEVER** commit `local.properties` to version control
- **NEVER** hardcode API keys in source code
- All keys are accessed through `SecretKeys` object
- Use the provided `ConfigValidator` to check setup

### 🚨 Troubleshooting

#### Build Issues
- Ensure `local.properties` exists and contains all required keys
- Clean and rebuild: `./gradlew clean build`
- Check that package name matches Firebase configuration

#### Runtime Issues
- Check logs for configuration validation messages
- Verify internet connectivity for external services
- Ensure proper permissions are granted

### 📱 Test Account Credentials

- **Email**: runiqhealthconnecttest@gmail.com
- **Firebase Project**: run-iq
- **Package Name**: com.runiq

---

For additional help, refer to the individual service documentation or check the configuration classes in `com.runiq.core.config`.