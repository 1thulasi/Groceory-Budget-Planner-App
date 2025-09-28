plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")  // Firebase Plugin
}

android {
    namespace = "com.example.smartgrocery"  // Change this to your package name
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.smartgrocery"  // Change this to your package name
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true  // ✅ Enable Jetpack Compose
    }

    buildFeatures {
        viewBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"  // ✅ Use the latest stable version
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.google.android.material:material:1.9.0")

    // ✅ Jetpack Compose BOM (Manages Compose Versions)
    implementation(platform("androidx.compose:compose-bom:2024.01.00"))

    // ✅ Required Compose dependencies
    implementation("androidx.compose.ui:ui")  // Core UI
    implementation("androidx.compose.material3:material3")  // Material 3 (Color, Theme)
    implementation("androidx.compose.ui:ui-tooling-preview")  // Preview Support
    debugImplementation("androidx.compose.ui:ui-tooling")  // Debug Tooling

    // ✅ Firebase Dependencies
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")

    // ✅ JSON Parsing
    implementation("org.json:json:20210307")

    // ✅ Unit Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
