rootProject.name = "sketchy"

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

include(":app")
include(":shaders")
include(":sketch")
include(":style")
