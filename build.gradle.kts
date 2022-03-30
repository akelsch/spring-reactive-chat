plugins {
    java
    id("org.springframework.boot") version "2.1.3.RELEASE"
    id("com.adarshr.test-logger") version "3.2.0"
    id("org.unbroken-dome.test-sets") version "4.0.0"
}

apply(plugin = "io.spring.dependency-management")

group = "de.htwsaar.vs"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.auth0:java-jwt:3.8.0")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.mockito:mockito-junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "junit", module = "junit")
    }
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo:2.2.0")
}

tasks.bootRun {
    if (project.hasProperty("args")) {
        args = (project.properties["args"] as String).split(",")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

testSets {
    create("integrationTest") {
        dirName = "it"
    }
}

tasks {
    check {
        dependsOn("integrationTest")
    }
}

tasks.named("integrationTest") {
    shouldRunAfter("test")
}
