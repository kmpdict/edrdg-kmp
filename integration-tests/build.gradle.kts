plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.detekt)
}

kotlin {
    jvmToolchain(21)

    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(project(":jmdict"))
            implementation(project(":jmnedict"))
            implementation(project(":kanjidic2"))
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}
