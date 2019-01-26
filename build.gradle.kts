plugins {
    java
    id("org.springframework.boot") version "2.1.2.RELEASE"
}

val javaJwtVersion by extra("3.6.0")

apply(plugin = "io.spring.dependency-management")

group = "de.htwsaar.vs"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.auth0:java-jwt:$javaJwtVersion")
    // Managed
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "junit", module = "junit")
    }
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.test {
    useJUnitPlatform()
}
