import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  idea
  kotlin("jvm") version "1.5.10"
  application
  id("org.jlleitschuh.gradle.ktlint") version "9.4.1"
  id("com.github.johnrengelman.shadow") version "6.1.0"
}

group = "com.shansown"
version = "1.0.0-SNAPSHOT"

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

val coroutineVersion = "1.4.3"
val vertxVersion = "4.0.0"
val junitJupiterVersion = "5.7.0"

val mainVerticleName = "com.shansown.vertx.MainVerticle"
val launcherClassName = "io.vertx.core.Launcher"

val watchForChange = "src/**/*"
val doOnChange = "${projectDir}/gradlew classes"

application {
  mainClass.set(launcherClassName)
}

apply(from = "$rootDir/gradle/quality/checkstyle.gradle")

dependencies {
  implementation(project(":common"))

  implementation(kotlin("stdlib-jdk8"))
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:$coroutineVersion")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$coroutineVersion")

  implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
  implementation("io.vertx:vertx-web-client")
  implementation("io.vertx:vertx-web")
  implementation("io.vertx:vertx-lang-kotlin")
  implementation("io.vertx:vertx-lang-kotlin-coroutines")

  implementation("org.slf4j:log4j-over-slf4j:1.7.30")
  implementation("org.slf4j:jul-to-slf4j:1.7.30")
  implementation("org.slf4j:slf4j-api:1.7.30")
  implementation("org.slf4j:jcl-over-slf4j:1.7.30")
  implementation("org.apache.logging.log4j:log4j-core:2.14.0")
  implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.14.0")

  testImplementation("io.vertx:vertx-junit5")
  testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    // see https://kotlinlang.org/docs/compiler-reference.html#jvm-target-version
    jvmTarget = "16"
    // https://kotlinlang.org/docs/compiler-reference.html#java-parameters
    javaParameters = true
  }
}

tasks.withType<ShadowJar> {
  archiveClassifier.set("fat")
  manifest {
    attributes(mapOf("Main-Verticle" to mainVerticleName))
  }
  mergeServiceFiles()
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging {
    events = setOf(PASSED, SKIPPED, FAILED)
  }
}

tasks.withType<JavaExec> {
  args = listOf("run", mainVerticleName, "--redeploy=$watchForChange", "--launcher-class=$launcherClassName", "--on-redeploy=$doOnChange")
}
