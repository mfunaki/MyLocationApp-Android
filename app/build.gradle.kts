import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

// secrets.properties ファイルを読み込むための準備
val secretsPropertiesFile = project.rootProject.file("secrets.properties")
val secretsProperties = Properties()

if (secretsPropertiesFile.exists() && secretsPropertiesFile.isFile) {
    try {
        FileInputStream(secretsPropertiesFile).use { fis ->
            secretsProperties.load(fis)
        }
    } catch (e: Exception) {
        println("Warning: Could not load secrets.properties file: ${e.message}")
    }
} else {
    println("Warning: secrets.properties file not found at ${secretsPropertiesFile.absolutePath}")
}

// secrets.properties から特定のキーの値を取得する関数
fun getSecret(propertyKey: String): String {
    val value = secretsProperties.getProperty(propertyKey)
    if (value.isNullOrEmpty()) {
        println("Warning: Secret key '$propertyKey' not found in secrets.properties or is empty.")
        return "" // または適切なデフォルト値やエラー処理
    }
    return value
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
        // APIキーをマニフェストプレースホルダーとして利用可能にする
        // "MAPS_API_KEY" というキー名で secrets.properties から値を取得
        manifestPlaceholders["MAPS_API_KEY"] = "\"${getSecret("MAPS_API_KEY")}\""
    }

    buildFeatures {
        buildConfig = true
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

secrets {
    // To add your Maps API key to this project:
    // 1. If the secrets.properties file does not exist, create it in the same folder as the local.properties file.
    // 2. Add this line, where YOUR_API_KEY is your API key:
    //        MAPS_API_KEY=YOUR_API_KEY
    propertiesFileName = "secrets.properties"

    // A properties file containing default secret values. This file can be
    // checked in version control.
    defaultPropertiesFileName = "local.defaults.properties"

    // Configure which keys should be ignored by the plugin by providing regular expressions.
    // "sdk.dir" is ignored by default.
    ignoreList.add("keyToIgnore") // Ignore the key "keyToIgnore"
    ignoreList.add("sdk.*")       // Ignore all keys matching the regexp "sdk.*"
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
    implementation("com.google.android.gms:play-services-maps:19.2.0")
    implementation("androidx.fragment:fragment-ktx:1.8.7")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}