# GitHub Secrets Configuration 🔐

This document outlines the required GitHub secrets for the RunIQ CI/CD pipeline.

## Required Secrets

### For CI/CD Pipeline

The following secrets need to be configured in your GitHub repository settings:

1. **ANDROID_KEYSTORE_FILE** (Base64 encoded)
   - Your release keystore file encoded in Base64
   - Generate with: `base64 -i your-release-key.jks | tr -d '\n'`

2. **ANDROID_KEYSTORE_PASSWORD**
   - Password for the keystore file

3. **ANDROID_KEY_ALIAS**
   - Alias of the key in the keystore

4. **ANDROID_KEY_PASSWORD**
   - Password for the key alias

### For API Integrations (Optional for CI)

These are used for integration testing with external services:

5. **SPOTIFY_CLIENT_ID**
   - Spotify application client ID

6. **SPOTIFY_CLIENT_SECRET**
   - Spotify application client secret

7. **ELEVEN_LABS_API_KEY**
   - Eleven Labs API key for text-to-speech

8. **GEMINI_API_KEY**
   - Google Gemini AI API key

9. **FIREBASE_SERVICE_ACCOUNT_KEY** (JSON)
   - Firebase service account key for backend operations

## Setting Up Secrets

### In GitHub Repository

1. Go to your repository on GitHub
2. Click on **Settings** tab
3. In the left sidebar, click **Secrets and variables** > **Actions**
4. Click **New repository secret**
5. Add each secret with the exact name listed above

### Local Development

For local development, create these files in your project root (they're already in `.gitignore`):

```bash
# API Keys (create these files with your actual keys)
echo "your_spotify_client_secret_here" > spotify_client_secret
echo "your_eleven_labs_api_key_here" > eleven_labs_api_key
echo "your_gemini_api_key_here" > gemini_api_key

# Keystore properties
cat > keystore.properties << EOF
storePassword=your_keystore_password
keyPassword=your_key_password
keyAlias=your_key_alias
storeFile=../path/to/your/keystore.jks
EOF
```

## Security Best Practices

- **Never commit secrets to version control**
- **Use environment-specific secrets** (dev, staging, prod)
- **Rotate secrets regularly**
- **Use least privilege principle** for service accounts
- **Monitor secret usage** in CI/CD logs
- **Use GitHub's secret scanning** to detect accidental commits

## CI/CD Usage

The GitHub Actions workflow uses these secrets to:

1. **Sign release builds** with the Android keystore
2. **Run integration tests** with external APIs
3. **Deploy to app stores** (when configured)
4. **Generate signed APKs** for distribution

## Troubleshooting

### Common Issues

1. **"Secret not found" error**
   - Verify secret name matches exactly (case-sensitive)
   - Check that secret is set at repository level, not organization level

2. **"Invalid keystore" error**
   - Ensure keystore file is properly Base64 encoded
   - Verify keystore password and alias are correct

3. **"API authentication failed"**
   - Check that API keys are valid and not expired
   - Verify API key has required permissions

### Debugging

To debug secret-related issues in CI:

1. Add temporary debug steps (remove after fixing):
   ```yaml
   - name: Debug secrets
     run: |
       echo "Keystore alias: ${{ secrets.ANDROID_KEY_ALIAS }}"
       echo "Has keystore file: ${{ secrets.ANDROID_KEYSTORE_FILE != '' }}"
   ```

2. Check GitHub Actions logs for specific error messages
3. Test secrets locally using the same commands as CI

---

**⚠️ Security Warning**: Never log actual secret values, only their presence or absence.