plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.martin.inputspy"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.martin.inputspy"
        minSdk = 33
        versionCode = 3
        versionName = "1.2"

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
    applicationVariants.all {
        val variant = this
        variant.outputs.all {
            val output = this
            // it's safe to force convert to child class, since [BaseVariantOutput] has only one direct child: [BaseVariantOutputImpl].
            val outputImpl = output as com.android.build.gradle.internal.api.BaseVariantOutputImpl
            val outputFile = output.outputFile
            if (outputFile?.name?.endsWith(".apk") == true) {
                val fileName = "Input_Spy_${buildType.name}_${defaultConfig.versionName}.apk"
                outputImpl.outputFileName = fileName
            }
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    compileOnly(files("libs\\aosp_u_framework.jar"))
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}