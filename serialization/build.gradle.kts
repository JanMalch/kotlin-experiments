plugins {
    // Apply the shared build logic from a convention plugin.
    // The shared code is located in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`.
    id("buildsrc.convention.kotlin-jvm")
    // Apply Kotlin Serialization plugin from `gradle/libs.versions.toml`.
    alias(libs.plugins.kotlinPluginSerialization)
    kotlin("plugin.allopen") version libs.versions.kotlin
    alias(libs.plugins.kotlinxBenchmark)
}

allOpen {
    annotation("org.openjdk.jmh.annotations.State")
}

sourceSets {
    create("benchmarks")
}

kotlin {
    /*
    Associate the benchmarks with the main compilation.
    This will:
    1. Allow 'benchmarks' to see all internals of 'main'
    2. Forward all dependencies from 'main' to be also visible in 'benchmarks'
     */
    target.compilations.getByName("benchmarks")
        .associateWith(target.compilations.getByName("main"))
}

dependencies {
    // Apply the kotlinx bundle of dependencies from the version catalog (`gradle/libs.versions.toml`).
    implementation(libs.androidxCollection)
    implementation(libs.bundles.kotlinxEcosystem)
    testImplementation(kotlin("test"))
    testImplementation(libs.junitEngine)
    testImplementation(libs.junitParams)
    "benchmarksImplementation"(libs.kotlinxBenchmark)
}

benchmark {
    targets {
        register("benchmarks")
    }
}
