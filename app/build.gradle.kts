plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.qr"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.qr"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }


}

dependencies {
    // AndroidX Core and other essential libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // ZXing (QR Code scanning)
    implementation("com.journeyapps:zxing-android-embedded:4.3.0") // ZXing pour Android

    implementation ("com.google.mlkit:barcode-scanning:17.0.2")
    // Gson for JSON parsing
    implementation("com.google.code.gson:gson:2.8.9")
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.recyclerview)

    // Testing dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.0") // Vérifiez la version la plus récente

    // LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.0") // Vérifiez la version la plus récente

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.0") // Vérifiez la version la plus récente

    implementation("androidx.fragment:fragment-ktx:1.6.1")

    implementation ("androidx.recyclerview:recyclerview:1.3.1") // Version la plus récente au moment de l'écriture

}
