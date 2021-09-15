import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  idea
  application
  kotlin("jvm") version "1.5.10"
  id("org.jlleitschuh.gradle.ktlint") version "9.4.1"
  id("org.jetbrains.kotlin.plugin.spring") version "1.4.31"
  id("org.springframework.boot") version "2.4.5"
}

repositories {
  mavenCentral()
}

ktlint {
  verbose.set(true)
  debug.set(false)
  reporters {
    reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
    reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.JSON)
  }
}

apply(from = "$rootDir/gradle/quality/checkstyle.gradle")
apply(plugin = "io.spring.dependency-management")

configurations {
  all {
    exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
  }
}

val coroutineVersion = "1.4.3"

dependencies {
  implementation(project(":common"))

  // Use the Kotlin JDK 8 standard library.
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:$coroutineVersion")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$coroutineVersion")

  // for Spring DI
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  // for Spring Reactive
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:$coroutineVersion")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$coroutineVersion")

  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.1")

  // Spring
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")

  // DB
  runtimeOnly("io.r2dbc:r2dbc-postgresql:0.8.7.RELEASE")
  runtimeOnly("org.postgresql:postgresql:42.2.19")

  // ULID
  implementation("de.huxhorn.sulky:de.huxhorn.sulky.ulid:8.2.0")

  implementation("org.slf4j:log4j-over-slf4j:1.7.30")
  implementation("org.slf4j:jul-to-slf4j:1.7.30")
  implementation("org.slf4j:slf4j-api:1.7.30")
  implementation("org.slf4j:jcl-over-slf4j:1.7.30")
  implementation("org.apache.logging.log4j:log4j-core:2.14.0")
  implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.14.0")

  testImplementation("org.jetbrains.kotlin:kotlin-test")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

application {
  // Define the main class for the application.
  mainClass.set("com.shansown.spring_webflux.SpringWebFlux")
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    // see https://kotlinlang.org/docs/compiler-reference.html#jvm-target-version
    jvmTarget = "16"
    // https://kotlinlang.org/docs/compiler-reference.html#java-parameters
    javaParameters = true
  }
}
