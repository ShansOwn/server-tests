import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  idea
  kotlin("jvm") version "1.5.10"
  application
  id("org.jlleitschuh.gradle.ktlint") version "9.4.1"
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

val coroutineVersion = "1.4.3"

dependencies {
  implementation(project(":common"))

  // Use the Kotlin JDK 8 standard library.
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:$coroutineVersion")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$coroutineVersion")

  // Javalin
  implementation("io.javalin:javalin:3.12.0")

  implementation("com.squareup.retrofit2:retrofit:2.9.0")
  implementation("com.squareup.retrofit2:converter-scalars:2.9.0")

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
  mainClass.set("com.shansown.javalin.Javalin")
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    // see https://kotlinlang.org/docs/compiler-reference.html#jvm-target-version
    jvmTarget = "16"
    // https://kotlinlang.org/docs/compiler-reference.html#java-parameters
    javaParameters = true
  }
}
