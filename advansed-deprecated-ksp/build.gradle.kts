val kspVersion: String by project
val kotlinpoet: String by project

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

    testImplementation("org.junit.jupiter:junit-jupiter:6.0.1")

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