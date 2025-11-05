plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt") // ✅ Enables annotation processing for Glide
}

android {
    namespace = "com.example.pokemon3"
    compileSdk = 35 // ✅ Updated to latest stable compile SDK

    defaultConfig {
        applicationId = "com.example.pokemon3"
        minSdk = 24
        targetSdk = 35 // ✅ Updated targetSdk
        versionCode = 1
        versionName = "1.0"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    // ✅ Explicit build tools version for stability
    buildToolsVersion = "35.0.0"
}

dependencies {
    // ✅ Core Android libraries (latest stable versions)
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.13.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")

    // ✅ RecyclerView (for lists)
    implementation("androidx.recyclerview:recyclerview:1.4.0")

    // ✅ LoopJ Async HTTP Client (stable)
    implementation("com.loopj.android:android-async-http:1.4.11")

    // ✅ Glide (latest stable, for image loading)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")

    // ✅ Testing Libraries
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}