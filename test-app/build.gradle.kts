plugins {
    id("com.google.devtools.ksp")
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project("::deprecated-lib"))
    ksp(project("::advanсed-deprecated-ksp"))
}

ksp {
    arg("currentVersion", "1.1.1")
}

//kotlin {
//    sourceSets.main {
//        kotlin.srcDirs(
//            "src/main/kotlin",
//            "build/generated/ksp/main/kotlin"
//        )
//    }
//}