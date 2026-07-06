import java.net.URI
import kotlinx.kover.gradle.plugin.dsl.AggregationType.COVERED_PERCENTAGE
import kotlinx.kover.gradle.plugin.dsl.CoverageUnit.BRANCH
import kotlinx.kover.gradle.plugin.dsl.CoverageUnit.INSTRUCTION
import kotlinx.kover.gradle.plugin.dsl.CoverageUnit.LINE
import kotlinx.kover.gradle.plugin.dsl.GroupingEntityType.APPLICATION
import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    alias(libs.plugins.kotlin.lang)
    alias(libs.plugins.test.fixtures)
    alias(libs.plugins.kotlinx.kover)
    alias(libs.plugins.sonarqube.scanner)
    alias(libs.plugins.thomas.logger)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency)
    `maven-publish`
    java
}

group = "com.thomas"
version = "1.0.0"
java.sourceCompatibility = JavaVersion.valueOf(libs.versions.target.get())
java.targetCompatibility = JavaVersion.valueOf(libs.versions.target.get())

kotlin {
    jvmToolchain(libs.versions.jdk.get().toInt())
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget.set(JvmTarget.valueOf(libs.versions.jvm.get()))
        freeCompilerArgs.addAll(
            "-Xjsr305=strict",
            "-Xcontext-parameters",
            "-opt-in=kotlin.RequiresOptIn",
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-java-parameters",
            "-Xconcurrent-gc"
        )
    }
}

dependencies {
    annotationProcessor(libs.spring.boot.autoconfigure.processor)
    annotationProcessor(libs.spring.boot.configuration.processor)

    implementation(libs.bundles.kotlin.stdlib.all)
    implementation(libs.bundles.kotlinx.modules.all)
    implementation(libs.bundles.log.logback.all)
    implementation(libs.bundles.thomas.lib.all)
    implementation(libs.bundles.spring.boot.all)
    implementation(libs.bundles.jackson.all)
    implementation(libs.bundles.moneta.all)

    compileOnly(libs.bundles.tomcat.all)
    compileOnly(libs.bundles.spring.compile.all)

    testImplementation(libs.bundles.junit.all)
    testImplementation(testFixtures(libs.thomas.core.lib))
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.bundles.tomcat.all)
}

tasks.test {
    useJUnitPlatform()
    systemProperty("file.encoding", "UTF-8")
    systemProperty("user.timezone", "UTC")
    maxHeapSize = "1g"

    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = FULL
    }
}

kover {
    currentProject {
        sources {
            excludedSourceSets.addAll("test", "testFixtures")
        }
    }
    reports {
        total {
            verify {
                onCheck = false
                rule("Branch Coverage of Tests must be more than 95%") {
                    disabled = false
                    groupBy = APPLICATION
                    bound {
                        aggregationForGroup = COVERED_PERCENTAGE
                        coverageUnits = BRANCH
                        minValue = 95
                    }
                }
                rule("Line Coverage of Tests must be more than 95%") {
                    disabled = false
                    groupBy = APPLICATION
                    bound {
                        aggregationForGroup = COVERED_PERCENTAGE
                        coverageUnits = LINE
                        minValue = 95
                    }
                }
                rule("Instruction Coverage of Tests must be more than 95%") {
                    disabled = false
                    groupBy = APPLICATION
                    bound {
                        aggregationForGroup = COVERED_PERCENTAGE
                        coverageUnits = INSTRUCTION
                        minValue = 95
                    }
                }
            }
            xml {
                onCheck = false
            }
            html {
                onCheck = false
            }
        }
    }
}

sonar {
    properties {
        property("sonar.sources", file("$projectDir/src/main/kotlin/"))
        property("sonar.tests", file("$projectDir/src/test/kotlin/"))
        property("sonar.projectName", "T.H.O.M.A.S. Spring Boot")
        property("sonar.projectKey", "thomas-spring-boot")
        property("sonar.token", System.getenv("SONAR_TOKEN"))
        property("sonar.host.url", System.getenv("SONAR_URL"))
        property("sonar.coverage.jacoco.xmlReportPaths", "${layout.buildDirectory.get()}/reports/kover/report.xml")
        property("sonar.kotlin.file.suffixes", ".kt")
        property("sonar.java.file.suffixes", ".java")
        property("sonar.verbose", true)
        property("sonar.qualitygate.wait", true)
    }
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            from(components["java"])
            artifact(sourcesJar)

            pom {
                packaging = "jar"
                name.set("T.H.O.M.A.S. Spring Boot")
                description.set("T.H.O.M.A.S. Spring Boot module for use in all other modules of the project.")
                licenses {
                    license {
                        name.set("MIT license")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        name.set("Nicanor Bondarenco")
                        email.set("nicanor_bondarenco@hotmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/NicoBondarenco/thomas-spring-boot.git")
                    developerConnection.set("scm:git:ssh://github.com/NicoBondarenco/thomas-spring-boot.git")
                    url.set("https://github.com/NicoBondarenco/thomas-spring-boot")
                }

            }
        }
    }
    repositories {
        maven {
            url = URI.create(System.getenv("REPOSITORY_PUBLISHER_URL")?:"http://localhost:9999/release")
            isAllowInsecureProtocol = true
            credentials {
                username = System.getenv("REPOSITORY_PUBLISHER_USERNAME")
                password = System.getenv("REPOSITORY_PUBLISHER_PASSWORD")
            }
        }
    }
}

tasks.named<BootJar>("bootJar") {
    enabled = false
}

tasks.named<Jar>("jar") {
    enabled = true
}
