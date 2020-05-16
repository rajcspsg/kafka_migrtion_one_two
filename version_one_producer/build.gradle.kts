plugins {
    java
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    maven(url ="https://mvnrepository.com/artifact/")
    mavenCentral()
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("org.apache.kafka", "kafka_2.12", "1.1.1")
    //implementation("com.sun.jdmk", "jmxtools", "1.2.1")
    testImplementation("junit", "junit", "4.12")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
