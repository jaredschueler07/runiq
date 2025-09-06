#!/bin/bash

# RunIQ Project Setup Script
# This script helps set up the development environment for RunIQ

set -e

echo "🚀 Setting up RunIQ development environment..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check if Android SDK is installed
check_android_sdk() {
    print_status "Checking Android SDK installation..."
    
    if [ -z "$ANDROID_HOME" ]; then
        print_warning "ANDROID_HOME not set. Please install Android SDK and set ANDROID_HOME."
        print_status "You can download Android Studio from: https://developer.android.com/studio"
        return 1
    fi
    
    if [ ! -d "$ANDROID_HOME" ]; then
        print_error "ANDROID_HOME directory does not exist: $ANDROID_HOME"
        return 1
    fi
    
    print_success "Android SDK found at: $ANDROID_HOME"
    return 0
}

# Check if Java 17+ is installed
check_java() {
    print_status "Checking Java installation..."
    
    if ! command -v java &> /dev/null; then
        print_error "Java not found. Please install JDK 17 or higher."
        return 1
    fi
    
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -lt 17 ]; then
        print_error "Java 17+ required. Found version: $JAVA_VERSION"
        return 1
    fi
    
    print_success "Java $JAVA_VERSION found"
    return 0
}

# Create API key files
setup_api_keys() {
    print_status "Setting up API key files..."
    
    # Create placeholder files if they don't exist
    if [ ! -f "spotify_client_secret" ]; then
        echo "your_spotify_client_secret_here" > spotify_client_secret
        print_warning "Created placeholder spotify_client_secret - please update with your actual key"
    fi
    
    if [ ! -f "eleven_labs_api_key" ]; then
        echo "your_eleven_labs_api_key_here" > eleven_labs_api_key
        print_warning "Created placeholder eleven_labs_api_key - please update with your actual key"
    fi
    
    if [ ! -f "gemini_api_key" ]; then
        echo "your_gemini_api_key_here" > gemini_api_key
        print_warning "Created placeholder gemini_api_key - please update with your actual key"
    fi
    
    if [ ! -f "app/google-services.json" ]; then
        print_warning "google-services.json not found in app/ directory"
        print_status "Please download it from Firebase Console and place it in app/ directory"
    fi
}

# Make gradlew executable
setup_gradle() {
    print_status "Setting up Gradle wrapper..."
    chmod +x ./gradlew
    print_success "Gradle wrapper is now executable"
}

# Run initial build and checks
run_initial_checks() {
    print_status "Running initial project checks..."
    
    # Check ktlint formatting
    print_status "Checking code formatting..."
    if ./gradlew ktlintCheck; then
        print_success "Code formatting check passed"
    else
        print_warning "Code formatting issues found. Run './gradlew ktlintFormat' to fix them."
    fi
    
    # Run tests
    print_status "Running unit tests..."
    if ./gradlew test; then
        print_success "Unit tests passed"
    else
        print_error "Unit tests failed. Please check the test output."
    fi
}

# Main setup function
main() {
    echo "======================================"
    echo "🏃‍♂️ RunIQ Development Setup"
    echo "======================================"
    echo
    
    # Check prerequisites
    if ! check_java; then
        exit 1
    fi
    
    if ! check_android_sdk; then
        print_warning "Android SDK not properly configured, but continuing..."
    fi
    
    # Setup project
    setup_gradle
    setup_api_keys
    
    # Run checks if SDK is available
    if [ ! -z "$ANDROID_HOME" ] && [ -d "$ANDROID_HOME" ]; then
        run_initial_checks
    else
        print_warning "Skipping build checks due to missing Android SDK"
    fi
    
    echo
    echo "======================================"
    print_success "Setup complete! 🎉"
    echo "======================================"
    echo
    print_status "Next steps:"
    echo "1. Update API key files with your actual keys"
    echo "2. Download google-services.json from Firebase Console"
    echo "3. Open the project in Android Studio"
    echo "4. Run './gradlew build' to verify everything works"
    echo
    print_status "For more information, see README.md"
}

# Run main function
main "$@"