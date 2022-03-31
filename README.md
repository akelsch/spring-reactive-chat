# spring-reactive-chat

Chat application built with Spring WebFlux

[Frontend built with Vue.js](https://github.com/nireinhard/VueChat)

## Requirements

- Java 17
- MongoDB 3.6+

## Getting Started

The project makes use of [MongoDB Change Streams](https://docs.mongodb.com/manual/changeStreams/) to stream new chat messages via a Server-Sent Events endpoint.

> Change Stream support is only possible for replica sets or for a sharded cluster.
>
> &mdash; [Spring Data MongoDB Reference Documentation](https://docs.spring.io/spring-data/mongodb/docs/2.1.5.RELEASE/reference/html/#change-streams)

Running a standalone MongoDB instance and calling the aforementioned endpoint will result in an exception:

> com.mongodb.MongoCommandException: Command failed with error 40573 (Location40573): 'The $changeStream stage is only supported on replica sets' on server localhost:27017.

To convert your standalone MongoDB instance into a replica set, please refer to this [guide](https://docs.mongodb.com/manual/tutorial/convert-standalone-to-replica-set/). Also, consider persisting the [`replication.replSetName`](https://docs.mongodb.com/manual/reference/configuration-options/#replication.replSetName) option in your [`mongod.conf`](https://docs.mongodb.com/manual/reference/configuration-options/#configuration-file) configuration file.

Other than that, no manual configuration is necessary 😄

## Building from Source

The project uses [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) and the [Gradle Kotlin DSL](https://docs.gradle.org/current/userguide/kotlin_dsl.html).

To build the project use

```Shell
./gradlew clean build
```

To run the project use

```Shell
./gradlew bootRun
```

To run the project with a specific profile, e.g. `prod`, use

```Shell
./gradlew bootRun --args='--spring.profiles.active=prod'
```

Note that multiple Spring arguments have to be separated by space:

```Shell
./gradlew bootRun --args='--spring.profiles.active=prod --spring.main.banner-mode=off'
```
