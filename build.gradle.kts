buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.android.gradle.plugin)
        classpath(libs.kotlin.gradle.plugin)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://raw.githubusercontent.com/kotlin-graphics/mary/master")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
