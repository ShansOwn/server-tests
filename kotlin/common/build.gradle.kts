plugins {
  idea
  `java-library`
  kotlin("jvm") version "1.5.10"
}

repositories {
  mavenCentral()
}

val coroutineVersion = "1.4.3"

dependencies {
  // Use the Kotlin JDK 8 standard library.
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:$coroutineVersion")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$coroutineVersion")

  // Ktor
  implementation("io.ktor:ktor-client-core:1.5.0")
  implementation("io.ktor:ktor-client-apache:1.5.0")

  implementation("com.squareup.retrofit2:retrofit:2.9.0")
  implementation("com.squareup.retrofit2:converter-scalars:2.9.0")

  implementation("org.slf4j:log4j-over-slf4j:1.7.30")
  implementation("org.slf4j:jul-to-slf4j:1.7.30")
  implementation("org.slf4j:slf4j-api:1.7.30")
  implementation("org.slf4j:jcl-over-slf4j:1.7.30")
  implementation("org.apache.logging.log4j:log4j-core:2.14.0")
  implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.14.0")
}
