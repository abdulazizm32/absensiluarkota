pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            credentials {
                username = "mapbox"
                password = "pk.eyJ1IjoiYWJkdWxheml6bXVydGFkaG8zMiIsImEiOiJjbWszZzdtdGQwcmZpM2RwdnAxeXdmNnRtIn0.HCe_tAXTv9i9c7WugsH0kw"
            }
        }
    }
}

rootProject.name = "Absensi Luar Kota"
include(":app")
