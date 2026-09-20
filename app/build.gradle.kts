plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.devtools.ksp)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ktlint.gradle)
    alias(libs.plugins.serialization.plugin)
    alias(libs.plugins.room)
}

room {
    schemaDirectory("debug", "$projectDir/schemas/debug")
    schemaDirectory("$projectDir/schemas")
}

val buildUniversal = project.findProperty("buildUniversal") == "true"

android {
    namespace = "io.github.vinnih.kipty"
    compileSdk = 36

    defaultConfig {
        applicationId = "io.github.vinnih.kipty"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "2.3.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }

    signingConfigs {
        create("release") {
            storeFile = file(System.getenv("KIPTY_KEYSTORE_PATH") ?: "release.keystore")

            if (storeFile != null) {
                storePassword = System.getenv("KIPTY_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("KIPTY_KEY_ALIAS")
                keyPassword = System.getenv("KIPTY_KEY_PASSWORD")

                require(!storePassword.isNullOrBlank()) {
                    "KIPTY_KEYSTORE_PASSWORD is required when KIPTY_KEYSTORE_PATH is set"
                }
                require(!keyAlias.isNullOrBlank()) {
                    "KIPTY_KEY_ALIAS is required when KIPTY_KEYSTORE_PATH is set"
                }
                require(!keyPassword.isNullOrBlank()) {
                    "KIPTY_KEY_PASSWORD or KIPTY_KEYSTORE_PASSWORD is required when KIPTY_KEYSTORE_PATH is set"
                }
            }
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
        }
        release {
            val releaseSigning = signingConfigs.findByName("release")
            if (releaseSigning?.storeFile != null) {
                signingConfig = releaseSigning
            }
            isShrinkResources = true
            isMinifyEnabled = true
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

    buildFeatures {
        compose = true
        buildConfig = true
    }

    splits {
        abi {
            isEnable = true
            reset()
            include("armeabi-v7a", "arm64-v8a")
            isUniversalApk = buildUniversal
        }
    }
}

val abiVersionCodes = mapOf(
    "armeabi-v7a" to 1,
    "arm64-v8a" to 2
)

androidComponents {
    onVariants(selector().withBuildType("release")) { variant ->
        variant.outputs.forEach { output ->
            val abiFilter = output.filters.find {
                it.filterType == com.android.build.api.variant.FilterConfiguration.FilterType.ABI
            }?.identifier
            val abiCode = abiVersionCodes[abiFilter]
            if (abiCode != null) {
                output.versionCode.set(abiCode * 1000 + (android.defaultConfig.versionCode ?: 1))
            }
        }
    }
}

ktlint {
    enableExperimentalRules.set(true)
}

dependencies {
    implementation(project(":vosk"))
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
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.media3.exoplayer)
    implementation(libs.media3.uicompose)
    implementation(libs.serialization.json)
    implementation(libs.worker.runtime.ktx)
    implementation(libs.google.accompanist.permission)
    implementation(libs.coil.compose)
    implementation(libs.ffmpeg.kit)
    implementation(libs.vosk.android)

    ksp(libs.androidx.hilt.compiler)
    ksp(libs.androidx.compiler)
    ksp(libs.hilt.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.androidx.arch.core.testing)

    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.mockk.android)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
