rootProject.name = "sketchy"

include(":app")

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
