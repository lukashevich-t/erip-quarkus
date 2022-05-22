plugins {
    java
    id("io.quarkus")
}

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        url = uri("sftp://git.gto.by:22002/var/mvnroot/")
        credentials {
            username = System.getProperty("mvn.deploy.username") // systemProp.mvn.deploy.username=name in c:\Users\<user>\.gradle\gradle.properties
            password = System.getProperty("mvn.deploy.password")
        }
    }
}

val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

dependencies {
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))
    implementation("by.gto.ais:ais-commons:1.11.0") {
        exclude("org.jetbrains.kotlin")
        exclude("com.fasterxml.jackson.core")
        exclude("com.fasterxml.jackson.datatype")
        exclude("javax.xml.bind")
    }
    implementation("commons-fileupload:commons-fileupload:1.4")
    implementation("io.quarkus:quarkus-jdbc-mariadb")
    implementation("io.quarkus:quarkus-hibernate-orm")
    implementation("io.quarkus:quarkus-undertow")
    implementation("io.quarkus:quarkus-agroal")
    implementation("io.quarkus:quarkus-mailer")
    implementation("io.quarkus:quarkus-smallrye-metrics")
    implementation("io.quarkus:quarkus-arc")
    testImplementation("io.quarkus:quarkus-junit5")
}

group = "by.gto.erip"
version = "1.0.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}
