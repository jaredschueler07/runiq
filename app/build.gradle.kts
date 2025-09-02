plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    
    // Compose Compiler plugin (required for Kotlin 2.0+)
    id("org.jetbrains.kotlin.plugin.compose")
    
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
    
    // Add Firebase Crashlytics plugin
    id("com.google.firebase.crashlytics")
    
    // Hilt dependency injection
    id("com.google.dagger.hilt.android")
    
    // Kotlin annotation processing
    id("kotlin-kapt")
}

android {
    namespace = "com.runiq"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.runiq"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // BuildConfig fields for API keys
        buildConfigField("String", "SPOTIFY_CLIENT_ID", "\"${project.findProperty("SPOTIFY_CLIENT_ID") ?: ""}\"")
        buildConfigField("String", "ELEVEN_LABS_API_KEY", "\"${project.findProperty("ELEVEN_LABS_API_KEY") ?: ""}\"")
        buildConfigField("String", "MAPS_API_KEY", "\"${project.findProperty("MAPS_API_KEY") ?: ""}\"")
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:34.2.0"))
    
    // Firebase dependencies (versions managed by BoM)
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.firebase:firebase-crashlytics")
    
    // Hilt Dependency Injection
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-compiler:2.51.1")
    
    // Jetpack Compose
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.8.2")
    
    // Compose debugging
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    
    // Core Android dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    
    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}