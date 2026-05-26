# Microsphere Logging Framework

> A unified abstraction layer for runtime logging management across multiple Java logging frameworks

[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/microsphere-projects/microsphere-logging)
[![Maven Build](https://github.com/microsphere-projects/microsphere-logging/actions/workflows/maven-build.yml/badge.svg)](https://github.com/microsphere-projects/microsphere-logging/actions/workflows/maven-build.yml)
[![Codecov](https://codecov.io/gh/microsphere-projects/microsphere-logging/branch/main/graph/badge.svg)](https://app.codecov.io/gh/microsphere-projects/microsphere-logging)
![Maven](https://img.shields.io/maven-central/v/io.github.microsphere-projects/microsphere-logging.svg)
![License](https://img.shields.io/github/license/microsphere-projects/microsphere-logging.svg)

## Introduction

Microsphere Logging Framework is a unified abstraction layer designed to simplify logging management across multiple
Java logging frameworks. It provides a consistent API for dynamically managing log levels at runtime, querying logger
information, exposing logging configuration over JMX, and integrating with various logging backends — all without
changing your application code or restarting services.

Supported backends: **Java Logging (JUL)**, **Log4j**, **Log4j2**, and **Logback**.

## Features

- **Runtime log level management** — Dynamically adjust logging levels in production without restarting services
- **Framework-agnostic API** — Write management code once; it works across all supported logging backends
- **JMX integration** — Expose and control logging configuration through JMX for operations teams
- **Comprehensive testing support** — JUnit 4 and JUnit Jupiter 5 extensions to test behavior across multiple log levels
- **SPI-based extensibility** — Load the highest-priority `Logging` implementation automatically via Java ServiceLoader
- **Multi-backend coexistence** — Use multiple logging backends in the same JVM with priority-based resolution

## Modules

| Module                             | Purpose                                                    |
|------------------------------------|------------------------------------------------------------|
| `microsphere-logging-parent`       | Parent POM with shared configurations                      |
| `microsphere-logging-dependencies` | BOM for managing dependency versions across the project    |
| `microsphere-logging-commons`      | Core API: `Logging` interface, `LoggingUtils`, JMX support |
| `microsphere-java-logging`         | Extension for Java Logging (JUL)                           |
| `microsphere-logback`              | Extension for Logback                                      |
| `microsphere-log4j`                | Extension for Log4j                                        |
| `microsphere-log4j2`               | Extension for Log4j2                                       |
| `microsphere-logging-test`         | JUnit 4 and JUnit Jupiter 5 test extensions                |
| `microsphere-logging-examples`     | Runnable examples                                          |

## Compatibility

| Requirement   | Version |
|---------------|---------|
| Java          | 8+      |
| JUnit 4       | 4+      |
| JUnit Jupiter | 5.13+   |
| SLF4J         | 1.7+    |
| Log4j         | 1.2+    |
| Logback       | 1.2+    |
| Log4j2        | 2.4+    |

## Getting Started

### 1. Import the BOM

Add the Microsphere Logging BOM to your `pom.xml` to manage all module versions consistently:

```xml

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.github.microsphere-projects</groupId>
            <artifactId>microsphere-logging-dependencies</artifactId>
            <version>${microsphere-logging.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### 2. Add the Backend Module

Pick **one** backend module that matches your logging framework:

```xml

<dependencies>
    <!-- Option A: Java Logging (JUL) -->
    <dependency>
        <groupId>io.github.microsphere-projects</groupId>
        <artifactId>microsphere-java-logging</artifactId>
    </dependency>

    <!-- Option B: Logback -->
    <dependency>
        <groupId>io.github.microsphere-projects</groupId>
        <artifactId>microsphere-logback</artifactId>
    </dependency>

    <!-- Option C: Log4j2 -->
    <dependency>
        <groupId>io.github.microsphere-projects</groupId>
        <artifactId>microsphere-log4j2</artifactId>
    </dependency>

    <!-- Option D: Log4j -->
    <dependency>
        <groupId>io.github.microsphere-projects</groupId>
        <artifactId>microsphere-log4j</artifactId>
    </dependency>
</dependencies>
```

### 3. Use the API

#### Load and query the active `Logging` instance

```java
import io.microsphere.logging.Logger;
import io.microsphere.logging.Logging;

import static io.microsphere.logging.LoggerFactory.getLogger;
import static io.microsphere.logging.LoggingUtils.load;

public class LoggingExample {

    private static final Logger logger = getLogger(LoggingExample.class);

    public static void main(String[] args) {
        // Loads the highest-priority Logging implementation available on the classpath
        Logging logging = load();
        logger.info("Active logging backend: {}", logging.getName());
        logger.info("All registered loggers: {}", logging.getLoggerNames());
    }
}
```

Output (with Log4j2 on the classpath):

```
[main] INFO io.microsphere.logging.examples.LoggingExample - Active logging backend: Log4j2
[main] INFO io.microsphere.logging.examples.LoggingExample - All registered loggers: [...]
```

#### Dynamically manage log levels at runtime

```java
Logging logging = load();

// Read the current level of a logger
String level = logging.getLoggerLevel("io.microsphere");   // e.g. "INFO"

// Change the level at runtime — no restart needed
logging.setLoggerLevel("io.microsphere", "DEBUG");

// Revert to inheriting level from the parent logger
logging.setLoggerLevel("io.microsphere", null);
```

#### Register logging configuration as JMX MBeans

```java
import io.microsphere.logging.jmx.LoggingMXBeanRegistrar;

// Registers one MBean per detected Logging backend under the
// "io.microsphere.logging:type=<BackendName>" ObjectName pattern
LoggingMXBeanRegistrar.registerAll();
```

Once registered, you can inspect and change log levels via any JMX client (e.g., JConsole, VisualVM, or
your operations toolchain).

More examples are available in the [microsphere-logging-examples](microsphere-logging-examples) module.

### Testing Support

#### JUnit Jupiter 5 — `@LoggingLevelsTest`

Run a test method once for each specified log level:

```java
import io.microsphere.logging.test.jupiter.LoggingLevelsTest;

class MyServiceTest {

    @LoggingLevelsTest(loggingClasses = {MyService.class}, levels = {"TRACE", "DEBUG", "INFO"})
    void testBehaviorAcrossLogLevels() {
        // This test body is executed three times, once per level
    }
}
```

#### JUnit 4 — `LoggingLevelsRule`

```java
import io.microsphere.logging.test.junit4.LoggingLevelsRule;
import org.junit.ClassRule;
import org.junit.Test;

public class MyServiceTest {

    @ClassRule
    public static final LoggingLevelsRule loggingLevelsRule =
            LoggingLevelsRule.levels("TRACE", "DEBUG", "INFO");

    @Test
    public void testBehaviorAcrossLogLevels() {
        // Executed for each log level defined in the rule
    }
}
```

## Building from Source

You only need to build from source if you want to try out the latest unreleased changes or contribute to the project.

```bash
# Clone the repository
git clone https://github.com/microsphere-projects/microsphere-logging.git
cd microsphere-logging

# Build (Linux/macOS)
./mvnw package

# Build (Windows)
mvnw.cmd package
```

## Getting Help

| Resource                       | Link                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        |
|--------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Bug reports & feature requests | [GitHub Issues](https://github.com/microsphere-projects/microsphere-logging/issues)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| Project wiki                   | [GitHub Wiki](https://github.com/microsphere-projects/microsphere-logging/wiki)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
| AI-powered docs                | [![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/microsphere-projects/microsphere-logging) [![zread](https://img.shields.io/badge/Ask_Zread-_.svg?style=flat&color=00b0aa&labelColor=000000&logo=data%3Aimage%2Fsvg%2Bxml%3Bbase64%2CPHN2ZyB3aWR0aD0iMTYiIGhlaWdodD0iMTYiIHZpZXdCb3g9IjAgMCAxNiAxNiIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHBhdGggZD0iTTQuOTYxNTYgMS42MDAxSDIuMjQxNTZDMS44ODgxIDEuNjAwMSAxLjYwMTU2IDEuODg2NjQgMS42MDE1NiAyLjI0MDFWNC45NjAxQzEuNjAxNTYgNS4zMTM1NiAxLjg4ODEgNS42MDAxIDIuMjQxNTYgNS42MDAxSDQuOTYxNTZDNS4zMTUwMiA1LjYwMDEgNS42MDE1NiA1LjMxMzU2IDUuNjAxNTYgNC45NjAxVjIuMjQwMUM1LjYwMTU2IDEuODg2NjQgNS4zMTUwMiAxLjYwMDEgNC45NjE1NiAxLjYwMDFaIiBmaWxsPSIjZmZmIi8%2BCjxwYXRoIGQ9Ik00Ljk2MTU2IDEwLjM5OTlIMi4yNDE1NkMxLjg4ODEgMTAuMzk5OSAxLjYwMTU2IDEwLjY4NjQgMS42MDE1NiAxMS4wMzk5VjEzLjc1OTlDMS42MDE1NiAxNC4xMTM0IDEuODg4MSAxNC4zOTk5IDIuMjQxNTYgMTQuMzk5OUg0Ljk2MTU2QzUuMzE1MDIgMTQuMzk5OSA1LjYwMTU2IDE0LjExMzQgNS42MDE1NiAxMy43NTk5VjExLjAzOTlDNS42MDE1NiAxMC42ODY0IDUuMzE1MDIgMTAuMzk5OSA0Ljk2MTU2IDEwLjM5OTlaIiBmaWxsPSIjZmZmIi8%2BCjxwYXRoIGQ9Ik0xMy43NTg0IDEuNjAwMUgxMS4wMzg0QzEwLjY4NSAxLjYwMDEgMTAuMzk4NCAxLjg4NjY0IDEwLjM5ODQgMi4yNDAxVjQuOTYwMUMxMC4zOTg0IDUuMzEzNTYgMTAuNjg1IDUuNjAwMSAxMS4wMzg0IDUuNjAwMUgxMy43NTg0QzE0LjExMTkgNS42MDAxIDE0LjM5ODQgNS4zMTM1NiAxNC4zOTg0IDQuOTYwMVYyLjI0MDFDMTQuMzk4NCAxLjg4NjY0IDE0LjExMTkgMS42MDAxIDEzLjc1ODQgMS42MDAxWiIgZmlsbD0iI2ZmZiIvPgo8cGF0aCBkPSJNNCAxMkwxMiA0TDQgMTJaIiBmaWxsPSIjZmZmIi8%2BCjxwYXRoIGQ9Ik00IDEyTDEyIDQiIHN0cm9rZT0iI2ZmZiIgc3Ryb2tlLXdpZHRoPSIxLjUiIHN0cm9rZS1saW5lY2FwPSJyb3VuZCIvPgo8L3N2Zz4K&logoColor=ffffff)](https://zread.ai/microsphere-projects/microsphere-logging) |

### JavaDoc

- [microsphere-logging-commons](https://javadoc.io/doc/io.github.microsphere-projects/microsphere-logging-commons)
- [microsphere-logging-test](https://javadoc.io/doc/io.github.microsphere-projects/microsphere-logging-test)
- [microsphere-java-logging](https://javadoc.io/doc/io.github.microsphere-projects/microsphere-java-logging)
- [microsphere-logback](https://javadoc.io/doc/io.github.microsphere-projects/microsphere-logback)
- [microsphere-log4j](https://javadoc.io/doc/io.github.microsphere-projects/microsphere-log4j)
- [microsphere-log4j2](https://javadoc.io/doc/io.github.microsphere-projects/microsphere-log4j2)

Before opening an issue,
please [search existing issues](https://github.com/microsphere-projects/microsphere-logging/issues)
to see if the problem has already been reported.

## Contributing

Contributions are welcome! To get started:

1. Read the [Code of Conduct](./CODE_OF_CONDUCT.md)
2. Fork the repository and create a feature branch
3. Make your changes and add tests
4. Submit a pull request with a clear description of the change

## Maintainers

Microsphere Logging is developed and maintained by the <a href="https://github.com/microsphere-projects">Microsphere
Projects</a> organization.

## License

Released under the [Apache License 2.0](LICENSE).
