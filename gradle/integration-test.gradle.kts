val sourceSets = the<SourceSetContainer>()

sourceSets {
    create("it") {
        java.srcDir(file("src/it/java"))
        resources.srcDir(file("src/it/resources"))
        compileClasspath += sourceSets["main"].output + configurations["testRuntimeClasspath"]
        runtimeClasspath += output + compileClasspath
    }
}

tasks.register<Test>("it") {
    description = "Runs the integration tests."
    group = "verification"
    testClassesDirs = sourceSets["it"].output.classesDirs
    classpath = sourceSets["it"].runtimeClasspath
    mustRunAfter(tasks["test"])
}

tasks.named("check") {
    dependsOn("it")
}
