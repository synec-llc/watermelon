plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.project.watermelon"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.project.watermelon"
        minSdk = 30
        targetSdk = 34
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.camera.view)
    implementation(libs.camera.lifecycle)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.android.volley:volley:1.2.1")
    // CameraX Dependencies
//    implementation ("androidx.camera:camera-core:1.5.0")
//    implementation ("androidx.camera:camera-camera2:1.5.0")
//    implementation ("androidx.camera:camera-lifecycle:1.5.0")
//    implementation ("androidx.camera:camera-view:1.5.0")
//    implementation ("androidx.camera:camera-extensions:1.5.0")

    implementation ("androidx.camera:camera-core:1.4.0")
    implementation ("androidx.camera:camera-view:1.4.0")
    implementation ("androidx.camera:camera-lifecycle:1.4.0")
    implementation ("androidx.camera:camera-extensions:1.4.0")
    implementation ("androidx.camera:camera-camera2:1.4.0")





}