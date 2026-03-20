# Microsphere Logging - Wiki

Welcome to the **Microsphere Logging** project wiki. This project provides a unified logging management abstraction that supports multiple Java logging frameworks through a consistent API and SPI-based architecture.

## Project Overview

| Property | Value |
|----------|-------|
| **Group ID** | `io.github.microsphere-projects` |
| **Version** | `0.1.1-SNAPSHOT` |
| **Java Version** | 8+ |
| **License** | Apache License 2.0 |
| **Author** | [Mercy](mailto:mercyblitz@gmail.com) |

## Architecture

Microsphere Logging uses the **Service Provider Interface (SPI)** pattern to dynamically discover and load logging framework implementations at runtime. Each framework adapter implements the core `Logging` interface and is registered via `META-INF/services/io.microsphere.logging.Logging`.

### Priority-Based Framework Selection

When multiple logging frameworks are present on the classpath, the framework with the highest priority is selected:

| Framework | Priority | Value |
|-----------|----------|-------|
| Java Logging (JDK) | `NORMAL_PRIORITY + 10` | Highest |
| Logback | `NORMAL_PRIORITY` | Default |
| Log4j | `NORMAL_PRIORITY - 3` | Lower |
| Log4j 2 | `NORMAL_PRIORITY - 5` | Lowest |

## Modules

### Core Module
- **[Logging Interface](Logging)** — The core management interface for logging facilities
- **[LoggingUtils](LoggingUtils)** — SPI-based loader utility for `Logging` implementations
- **[LoggingLevelsResolver](LoggingLevelsResolver)** — Interface and default implementation for resolving log levels
- **[LoggingMXBeanAdapter](LoggingMXBeanAdapter)** — JMX adapter for the `Logging` interface
- **[LoggingMXBeanRegistrar](LoggingMXBeanRegistrar)** — Registers `Logging` instances as JMX MBeans

### Framework Adapters
- **[JavaLogging](JavaLogging)** — Adapter for Java's built-in logging (`java.util.logging`)
- **[Log4jLogging](Log4jLogging)** — Adapter for Apache Log4j 1.x
- **[Log4j2Logging](Log4j2Logging)** — Adapter for Apache Log4j 2.x
- **[LogbackLogging](LogbackLogging)** — Adapter for Logback

### Utility Classes
- **[Log4j LoggerUtils](Log4j-LoggerUtils)** — Utility methods for Log4j 1.x logger operations
- **[Log4j2 LoggerUtils](Log4j2-LoggerUtils)** — Utility methods for Log4j 2.x logger operations
- **[Logback LoggerUtils](Logback-LoggerUtils)** — Utility methods for Logback logger operations

### Testing Support
- **[LoggingLevelsTest (JUnit 5)](LoggingLevelsTest)** — Test template annotation for repeating tests across logging levels
- **[LoggingLevelsClass (JUnit 5)](LoggingLevelsClass)** — Class template annotation for repeating test classes across logging levels
- **[LoggingLevelsRule (JUnit 4)](LoggingLevelsRule)** — JUnit 4 `TestRule` for iterating through logging levels

### Examples
- **[LoggingExample](LoggingExample)** — Demonstrates basic usage of the logging abstraction

## Dependency Versions

| Dependency | Version (Java 11+) | Version (Java 8–10) |
|------------|---------------------|---------------------|
| SLF4j | 2.0.17 | 1.7.36 |
| Log4j 1.x | 1.2.17 | 1.2.17 |
| Log4j 2.x | 2.25.3 | 2.25.3 |
| Logback | 1.5.32 | 1.2.12 |
| JUnit 4 | 4.13.2 | 4.13.2 |
| JUnit 5 (Jupiter) | 6.0.3 | 5.14.3 |

## Quick Start

### Maven Dependency

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-logging-commons</artifactId>
    <version>0.1.1-SNAPSHOT</version>
</dependency>
```

Add the framework adapter you need:

```xml
<!-- For Logback -->
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-logback</artifactId>
    <version>0.1.1-SNAPSHOT</version>
</dependency>

<!-- For Log4j 2.x -->
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-log4j2</artifactId>
    <version>0.1.1-SNAPSHOT</version>
</dependency>
```

### Basic Usage

```java
import io.microsphere.logging.Logging;
import static io.microsphere.logging.LoggingUtils.load;

public class QuickStart {
    public static void main(String[] args) {
        // Load the highest-priority Logging implementation
        Logging logging = load();

        // Print framework name
        System.out.println("Framework: " + logging.getName());

        // List all loggers
        System.out.println("Loggers: " + logging.getLoggerNames());

        // Manage log levels
        logging.setLoggerLevel("com.example.myapp", "DEBUG");
        String level = logging.getLoggerLevel("com.example.myapp");
        System.out.println("Level: " + level);
    }
}
```
