pluginManagement {
    repositories {
        gradlePluginPortal()
    }

    plugins {
        kotlin("multiplatform").version("1.9.25")
    }
}

includeBuild("convention-plugins")
