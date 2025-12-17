plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ktlint.gradle)
    alias(libs.plugins.serialization.plugin)
}

android {
    namespace = "io.github.vinnih.kipty"
    compileSdk = 36

    defaultConfig {
        applicationId = "io.github.vinnih.kipty"
        minSdk = 26
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs = listOf("-XXLanguage:+PropertyParamAnnotationDefaultTargetMode")
    }
    buildFeatures {
        compose = true
    }
}

ktlint {
    enableExperimentalRules.set(true)
}

dependencies {
    implementation(project(":lib"))
    implementation(libs.android.transcoder)
    implementation(libs.android.resampler)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.room)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.navigation3.runtime)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.hilt.worker)
    implementation(libs.androidx.splashscreen)
    implementation(libs.google.fonts)
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.uicompose)
    implementation(libs.serialization.json)
    implementation(libs.lottie.compose)
    implementation(libs.worker.runtime.ktx)
    implementation(libs.google.accompanist.permission)

    ksp(libs.androidx.hilt.compiler)
    ksp(libs.androidx.compiler)
    ksp(libs.hilt.compiler)

    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))

    debugImplementation(libs.androidx.compose.ui.tooling)

    ktlintRuleset(libs.ktlint.compose)
}
