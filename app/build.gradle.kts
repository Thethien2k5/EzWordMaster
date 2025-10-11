plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0"
    id("kotlinx-serialization")
}

android {
    namespace = "com.example.ezwordmaster"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.ezwordmaster"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

}

kotlin {
    jvmToolchain(17)
}


dependencies {
    // Compose BOM
    implementation(platform("androidx.compose:compose-bom:2024.05.00"))

    // Compose core
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    // Activity Compose
    implementation("androidx.activity:activity-compose:1.9.0")

    // Optional: Icons
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")

    // Debug tools
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    //
    implementation("androidx.navigation:navigation-compose:2.7.5")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
}
