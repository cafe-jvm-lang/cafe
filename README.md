# CAFE: Programming Language

[![Build](https://github.com/cafe-jvm-lang/cafe/workflows/Build/badge.svg)](https://github.com/cafe-jvm-lang/cafe/actions?query=workflow%3ABuild)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Github All Releases](https://img.shields.io/github/downloads/cafe-jvm-lang/cafe/total.svg)](#)
[![Join the chat at https://gitter.im/cafe-jvm-lang/cafe-lang](https://badges.gitter.im/cafe-jvm-lang/cafe-lang.svg)](https://gitter.im/cafe-jvm-lang/cafe-lang?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

A simple, dynamic, weakly typed, prototype based language for the JVM. 
Built with `invokedynamic` instruction introduced in Java 7, Cafe takes advantage of latest advances of JVM.

This project is developed with an intent to explore `how a programming language runs internally` and is also a major project for the graduation year.

---
**NOTE**

This project is still under development.

---

## Authors
In order to get in touch with Cafe Authors, join
[cafe-jvm-lang/cafe-lang](https://gitter.im/cafe-jvm-lang/cafe-lang) (Gitter).
|                                                                                                 | username                                                       | talk to me about...                               |
--------------------------------------------------------------------------------------------------|----------------------------------------------------------------|---------------------------------------------------|
 <img src="https://avatars.githubusercontent.com/dhyey-shah"         height="50px" title="Dhyey Shah"/>           | [`@dhyey-shah`](https://github.com/dhyey-shah)                 | cafe grammar,semantic-analyzer,compiler back-end (IR,code-generation), runtime       |
 <img src="https://avatars.githubusercontent.com/SaurabhPethani"         height="50px" title="Saurabh Pethani"/>           | [`@SaurabhPethani`](https://github.com/SaurabhPethani)                 | compiler front-end (RD parser, ast)       |
 <img src="https://avatars.githubusercontent.com/supastrikaromil"         height="50px" title="Romil Nisar"/>           | [`@supastrikaromil`](https://github.com/supastrikaromil)                 | cafe grammar, compiler front-end (Tokenizer)       |

## Building CAFE
Cafe is built with [Gradle](https://gradle.org).
Since the source code contains the [Gradle wrapper scripts](https://docs.gradle.org/current/userguide/gradle_wrapper.html),
the build can bootstrap itself by downloading the qualified Gradle version from the Internet.

### Java virtual machine compatibility

Cafe requires Java 8 to build.

In practice you can run most Cafe code with Java 8 and beyond.

### Building

This project is built with [IntelliJ IDEA](https://www.jetbrains.com/idea/) and can be loaded as a gradle project. I believe any other IDEs which support gradle can load this project.

#### Gradle Java plugin

For running as a java project, [java-application-plugin](https://docs.gradle.org/current/userguide/java_plugin.html) is already included and can be invoked with `gradlew run`.
The source file to compile can be set from `build.gradle` `run` method.

## License
GNU General Public License v3.0.

See LICENSE to see the full text.

## Contributing

We are cuurently closed for contributions.
