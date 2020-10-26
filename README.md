# CAFE: Programming Language
A simple, dynamic, weakly typed, prototype based language for the JVM.
Built with `invokedynamic` instruction introduced in Java 7, Cafe takes advantage of latest advances of JVM.

This project is developed with intent to learn and explore `how a programming language runs internally` and is also a major project for the graduation year.

## Building CAFE
Cafe is built with [Gradle](https://gradle.org).
Since the source code contains the [Gradle wrapper scripts](https://docs.gradle.org/current/userguide/gradle_wrapper.html),
the build can bootstrap itself by downloading the qualified Gradle version from the Internet.

### Java virtual machine compatibility

Cafe requires Java 8 to build.

In practice you can run most Cafe code with Java 11 and beyond.

### Building

This project is built with [IntelliJ IDEA](https://www.jetbrains.com/idea/) and can be loaded as a gradle project. I believe any other IDEs which support gradle can load this project.

#### Gradle Java plugin

For running as a java project, [java-application-plugin](https://docs.gradle.org/current/userguide/java_plugin.html) is already included and can be invoked with `gradlew run`.
The source file to compile can be set from `build.gradle` `run` method.

For now, testing is not done, though [JUnit](https://junit.org/) framework is installed with a view to start testing on later phases of development.

## Contributing

We are cuurently closed for contributions.