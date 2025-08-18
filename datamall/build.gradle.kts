import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.serialization)
    // Build config
    alias(libs.plugins.gmazzo.buildconfig)
}

kotlin {
    androidTarget()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "datamall"
            isStatic = true
            binaryOption("bundleId", "com.cherry.kmp.datamall")
        }
    }

    sourceSets {
        all {
            languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(kotlin("test-common"))
            implementation(kotlin("test-annotations-common"))
        }
        
        androidUnitTest.dependencies {
            implementation(kotlin("test-junit"))
            implementation("junit:junit:4.13.2")
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }

        @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            api(compose.materialIconsExtended)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)

            //Common viewmodel
            implementation(libs.lifecycle.viewmodel.compose)

            //Navigation
            implementation(libs.androidx.navigation.compose)

            //Koin
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            // required by koin
            implementation(libs.stately.common)

            //Ktor
            implementation(libs.ktor.core)
            implementation(libs.ktor.json)
            implementation(libs.ktor.logging)
            implementation(libs.ktor.negotiation)
            implementation(libs.kotlinx.serialization.json)

            //Coil
            implementation(libs.coil)
            implementation(libs.coil.network.ktor)
            implementation(libs.coil.compose.core)
            implementation(libs.coil.compose)
            
            //Kermit Logging
            implementation(libs.kermit)
            
            // Core module dependency
            api(project(":core"))
            
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.cherry.kmp.datamall"
    compileSdk = 35

    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        minSdk = 28
    }
    
    buildTypes {
        getByName("debug") {
            // Debug builds have logging enabled
        }
        getByName("release") {
            // Release builds have logging disabled
            isMinifyEnabled = false
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

// Read environment variables from .env file
val envFile = project.rootProject.file(".env")
val envProperties = Properties()
if (envFile.exists()) {
    envProperties.load(envFile.inputStream())
}

// Helper function to get environment variable with fallback
fun getEnvVar(key: String, defaultValue: String = ""): String {
    return envProperties.getProperty(key) ?: System.getenv(key) ?: defaultValue
}

buildConfig {
    buildConfigField("DATAMALL_API_KEY", getEnvVar("DATAMALL_API_KEY", ""))
}