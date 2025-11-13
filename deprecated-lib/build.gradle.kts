val kspVersion: String by project

plugins {
    kotlin("jvm")
    `maven-publish`
}

//dependencies {
//    kotlin("org.jetbrains.kotlin:kotlin-stdlib")
//}
repositories {
    mavenCentral()
}

publishing{
    publications{
        create<MavenPublication>("maven") {
            groupId = "ru.vood.advansed.deprecated"
            artifactId = "advansed-deprecated"
            version = "1.0.0"

            from(components["java"])
        }
    }
}
