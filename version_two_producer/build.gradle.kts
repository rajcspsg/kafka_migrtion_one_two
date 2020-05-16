plugins {
    java
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("org.apache.kafka", "kafka_2.12", "2.0.0")
    implementation("com.yammer.metrics", "metrics-graphite", "2.2.0")
    testImplementation("junit", "junit", "4.12")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

task("runMain", JavaExec::class) {
    main = "KafkaMetricsApp2"
    classpath = sourceSets["main"].runtimeClasspath
}