import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.Internal
import org.gradle.process.ExecOperations
import javax.inject.Inject

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.ecclesia.android"
    compileSdk {
        version = release(37) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.ecclesia.android"
        minSdk = 27
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField("String", "API_BASE_URL", "\"http://127.0.0.1:8000/api/v1/\"")
        }
        release {
            buildConfigField("String", "API_BASE_URL", "\"https://api.ecclesia.com/api/v1/\"")
            optimization {
                enable = false
            }
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
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

val localProps = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}
val adbExe = listOf(
    localProps.getProperty("sdk.dir")?.let { file("$it/platform-tools/adb.exe") },
    localProps.getProperty("sdk.dir")?.let { file("$it/platform-tools/adb") }
).firstOrNull { it != null && it.exists() }?.absolutePath ?: "adb"

abstract class AdbReverseTask : DefaultTask() {
    @get:Inject
    abstract val execOps: ExecOperations

    @get:Internal
    var adbPath: String = "adb"

    @TaskAction
    fun reverse() {
        try {
            execOps.exec {
                commandLine(adbPath, "reverse", "tcp:8000", "tcp:8000")
            }
        } catch (e: Exception) {
            println("aviso: no se pudo crear adb reverse tcp:8000 (¿hay emulador/dispositivo conectado?)")
        }
    }
}

tasks.register("adbReverseBackend", AdbReverseTask::class).configure {
    adbPath = adbExe
}

tasks.matching { it.name.startsWith("installDebug") || it.name.startsWith("installRelease") }.configureEach {
    dependsOn("adbReverseBackend")
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.datastore.preferences)

    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.coil.compose)

    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.navigation.testing)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
