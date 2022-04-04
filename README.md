# spring-reactive-chat

Chat application built with Spring WebFlux

[Frontend built with Vue.js](https://github.com/nireinhard/VueChat)

## Requirements

- Java 17
- MongoDB 3.6+

## Getting Started

### Building from Source

This project uses [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) and the [Gradle Kotlin DSL](https://docs.gradle.org/current/userguide/kotlin_dsl.html).

To build the project use:

```Shell
gradlew clean build
```

### Running the Application

To run the application you can use one of the tasks provided by the Spring Boot Gradle Plugin:

```Shell
gradlew bootRun
```

Additionally, you can also set specific application profiles, e.g. for `dev` use:

```Shell
gradlew bootRun --args='--spring.profiles.active=dev'
```

## Using MongoDB

The project makes use of [MongoDB Change Streams](https://www.mongodb.com/docs/manual/changeStreams/) to stream new chats and chat messages via Server-Sent Events (SSE) endpoints based on the `text/event-stream` media type.

> Change Stream support is only possible for replica sets or for a sharded cluster.
>
> &mdash; [Spring Data MongoDB Reference Documentation](https://docs.spring.io/spring-data/mongodb/docs/3.3.3/reference/html/#change-streams)

Running a standalone MongoDB instance and calling the aforementioned endpoint will result in an exception:

> com.mongodb.MongoCommandException: Command failed with error 40573 (Location40573): 'The $changeStream stage is only supported on replica sets' on server localhost:27017.

Please refer to this [tutorial](https://www.mongodb.com/docs/manual/tutorial/deploy-replica-set/) to deploy a replica set.

Feel free to use the included [Docker Compose file](docker-compose.yml) to get started locally.
