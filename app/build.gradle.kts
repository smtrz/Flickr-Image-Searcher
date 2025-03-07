plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.ksp)
    kotlin("kapt")
}

android {
    namespace = "com.tahir.flickrimagesearcher"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.tahir.flickrimagesearcher"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    buildFeatures {
        buildConfig = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.5"
    }

    ksp {
        arg("KOIN_CONFIG_CHECK", "true")
        arg("KOIN_DEFAULT_MODULE", "false")

    }

    sourceSets.getByName("main") {
        java.srcDirs("${layout.buildDirectory}/generated/ksp/main/kotlin")
    }
}

dependencies {
    // standard
    implementation(libs.bundles.androidstandard)

    // Kotlin Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Networking
    implementation(libs.bundles.networking)

    // Dependency Injection (Koin)
    implementation(libs.bundles.di)
    ksp(libs.koin.ksp.compiler)

    // Room Database
    implementation(libs.room.runtime)
    kapt(libs.room.compiler)

    // Logging
    implementation(libs.timber)

    // Jetpack Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.bundles.compose)

    // Testing
    testImplementation(libs.bundles.unittest)
    androidTestImplementation(libs.bundles.androidtest)
}
