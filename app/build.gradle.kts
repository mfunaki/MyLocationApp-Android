plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "mayoct.net.mylocationapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "mayoct.net.mylocationapp"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

androidComponents {
    onVariants { variant -> // すべてのビルドバリアント (debug, releaseなど) が対象
        val baseName = "MyLocationApp" // ここに希望するアプリの基本名を設定
        val variantName = variant.name // ビルドバリアント名 (例: "debug", "release", "flavorNameDebug")

        // versionName を取得 (AGP 8.0+ で推奨される方法)
        // variant.outputs.firstOrNull()?.versionName は Provider<String> を返すので .getOrNull() で値を取得
        val versionName = variant.outputs.firstOrNull()?.versionName?.getOrNull()
            ?: project.android.defaultConfig.versionName // フォールバックとしてdefaultConfigの値を使用
            ?: "unknown" // それでも取得できない場合のデフォルト値

        val newApkName = "${baseName}-${variantName}-${versionName}.apk"
        // println("Setting APK name for ${variant.name} to $newApkName") // デバッグ用ログ

        variant.outputs.forEach { output ->
            if (output.outputType.name == "MAIN") {
                // output.outputFileName は Property<String> なので .set() で値を設定
                // output.outputFileName.set(newApkName)
            }
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation("com.google.android.gms:play-services-location:21.3.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}