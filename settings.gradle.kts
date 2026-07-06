rootProject.name = "thomas-spring-boot"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven {
            url = uri(System.getenv("REPOSITORY_READER_URL"))
            isAllowInsecureProtocol = true
            credentials {
                username = System.getenv("REPOSITORY_READER_USERNAME")
                password = System.getenv("REPOSITORY_READER_PASSWORD")
            }
        }
        mavenLocal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven {
            url = uri(System.getenv("REPOSITORY_READER_URL"))
            isAllowInsecureProtocol = true
            credentials {
                username = System.getenv("REPOSITORY_READER_USERNAME")
                password = System.getenv("REPOSITORY_READER_PASSWORD")
            }
        }
        mavenLocal()
    }
}

buildCache {
    local {
        isEnabled = false
        directory = File(rootDir, "build-cache")
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
