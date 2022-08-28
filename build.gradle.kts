buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    java
    `maven-publish`
}

group = "dev.denux"
val archivesBaseName = "dtp"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

publishing {
    publications {
        register("Release", MavenPublication::class) {
            from(components["java"])

            artifactId = archivesBaseName
            groupId = group as String
            version = version as String
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

tasks.withType<Test>{ useJUnitPlatform() }
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}