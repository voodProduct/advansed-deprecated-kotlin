val kspVersion: String by project
val kotlinpoet: String by project
val kotestVersion: String by project


plugins {
    kotlin("jvm")
    `maven-publish`
//    id("maven-publish")
}

dependencies {
    implementation(project(":deprecated-lib"))
    implementation("com.google.devtools.ksp:symbol-processing-api:$kspVersion")
    implementation("com.google.devtools.ksp:symbol-processing:$kspVersion")
    implementation("com.squareup:kotlinpoet:${kotlinpoet}")
    implementation("com.squareup:kotlinpoet-ksp:${kotlinpoet}")

//    testImplementation("org.junit.jupiter:junit-jupiter:6.0.1")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
//    testImplementation("io.kotest:kotest-framework-datatest:$kotestVersion")
    testImplementation("io.kotest:kotest-framework-engine:${kotestVersion}")
}
repositories {
    mavenCentral()
}

publishing{
    publications{
        create<MavenPublication>("maven") {
            groupId = "ru.vood.advansed.deprecated"
            artifactId = "advansed-deprecated-ksp"
            version = "1.0.0"

            from(components["java"])
        }
    }
}
//publishing {
//    publications {
//        maven(MavenPublication) {
//            groupId 'org.gradle.sample'
//            artifactId 'project1-sample'
//            version '1.1'
//            from components.java
//        }
//    }
//}