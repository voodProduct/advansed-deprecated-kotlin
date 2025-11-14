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
            groupId = "ru.vood.advanced.deprecated"
            artifactId = "advanced-deprecated"
            version = "1.0.0"

            from(components["java"])
        }
    }
}
