plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.potholeapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.potholeapplication"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures{
        viewBinding = true
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.mpandroidchart)
    implementation(libs.retrofit)
    implementation(libs.android)
    implementation(libs.gson)
    implementation(libs.converter.gson)
    implementation(libs.lottie)
    implementation(libs.circleimageview)
    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)
    implementation(libs.navigation)
    implementation(libs.ui.maps)
    implementation(libs.navigationcore.android)
    implementation(libs.place.autocomplete)
    implementation(libs.mapbox.search.android.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)


}