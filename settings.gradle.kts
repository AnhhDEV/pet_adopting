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
            credentials.username = "mapbox"
            credentials.password = "sk.eyJ1IjoiYm9zbW9kZWwiLCJhIjoiY201eGE2MnF0MDU0MzJ3cjRjM3M5ZDJmOSJ9.opqiAhYvebh-LHq8G8OSkA"
            authentication.create<BasicAuthentication>("basic")
        }
    }
}

rootProject.name = "PetAdopt"
include(":app")
 