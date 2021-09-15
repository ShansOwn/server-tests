pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

rootProject.name = "kotlin-server"
include("common", "spark", "javalin", "ktor", "spring", "spring-webflux", "vertx", "dropwizard")
