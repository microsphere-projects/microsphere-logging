# Log4jLogging

## Overview

`Log4jLogging` is the `Logging` implementation for the Apache Log4j 1.x framework. It provides log level management through the Log4j `LoggerRepository` and related utilities.

## Details

| Property | Value |
|----------|-------|
| **Full Qualified Name** | `io.microsphere.logging.log4j.Log4jLogging` |
| **Type** | Class |
| **Module** | `microsphere-log4j` |
| **Package** | `io.microsphere.logging.log4j` |
| **Implements** | `io.microsphere.logging.Logging` |
| **Priority** | `NORMAL_PRIORITY - 3` (7) |
| **Since** | 1.0.0 |
| **Author** | [Mercy](mailto:mercyblitz@gmail.com) |

## Explanation

`Log4jLogging` adapts the legacy Apache Log4j 1.x framework to the Microsphere `Logging` interface. It manages loggers through the Log4j `LoggerRepository` and delegates level operations to `io.microsphere.logging.log4j.util.LoggerUtils`.

### Key Characteristics

| Characteristic | Value |
|---------------|-------|
| **Framework Name** | `"Log4j"` |
| **Root Logger Name** | `"root"` |
| **Supported Levels** | OFF, FATAL, ERROR, WARN, INFO, DEBUG, TRACE, ALL |
| **Priority** | `NORMAL_PRIORITY - 3` = 7 |

### Internal Delegation

| Method | Delegates To |
|--------|-------------|
| `getLoggerNames()` | `LoggerRepository.getCurrentLoggers()` — enumerates all loggers |
| `getLoggerLevel(String)` | `LoggerUtils.getLevelString(String)` |
| `setLoggerLevel(String, String)` | `LoggerUtils.setLoggerLevel(String, String)` |

### SPI Registration

Registered in `META-INF/services/io.microsphere.logging.Logging`:
```
io.microsphere.logging.log4j.Log4jLogging
```

### Related Components

- **`Log4jLogger`** — A package-private adapter wrapping `org.apache.log4j.Logger` that extends `AbstractLogger` and implements `DelegatingWrapper`. Provides `trace`, `debug`, `info`, `warn`, `error` methods with enabled checks.
- **`Log4jLoggerFactory`** — Factory class extending `LoggerFactory` that creates `Log4jLogger` instances. Registered via SPI at `META-INF/services/io.microsphere.logging.LoggerFactory`.
- **`LoggerUtils`** — Utility class providing static methods for logger retrieval and level management (see [Log4j LoggerUtils](Log4j-LoggerUtils)).

## Example Code

### Basic Usage

```java
import io.microsphere.logging.Logging;
import io.microsphere.logging.log4j.Log4jLogging;

public class Log4jExample {
    public static void main(String[] args) {
        Logging logging = new Log4jLogging();

        System.out.println("Name: " + logging.getName());
        // Output: Name: Log4j

        System.out.println("Root Logger: " + logging.getRootLoggerName());
        // Output: Root Logger: root

        System.out.println("Supported Levels: " + logging.getSupportedLoggingLevels());
        // Output: [OFF, FATAL, ERROR, WARN, INFO, DEBUG, TRACE, ALL]

        // Change a logger's level at runtime
        logging.setLoggerLevel("com.example.myapp", "DEBUG");
        System.out.println("Level: " + logging.getLoggerLevel("com.example.myapp"));
        // Output: Level: DEBUG

        // List all registered loggers
        System.out.println("Loggers: " + logging.getLoggerNames());
    }
}
```

### Auto-Discovery via SPI

```java
import io.microsphere.logging.Logging;
import io.microsphere.logging.LoggingUtils;
import java.util.List;

public class Log4jDiscoveryExample {
    public static void main(String[] args) {
        // If Log4j 1.x is on the classpath, Log4jLogging will be available
        List<Logging> all = LoggingUtils.loadAll();
        for (Logging logging : all) {
            if ("Log4j".equals(logging.getName())) {
                System.out.println("Log4j is available with priority: " + logging.getPriority());
            }
        }
    }
}
```

## Version Compatibility

| Version | Java | Log4j Version | Notes |
|---------|------|--------------|-------|
| 1.0.0+ | 8+ | 1.2.17 | Log4j 1.x dependency is optional |

### Maven Dependency

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-log4j</artifactId>
    <version>0.1.1-SNAPSHOT</version>
</dependency>
```

**Note**: Log4j 1.x (`log4j:log4j:1.2.17`) is declared as an optional dependency. You must include it explicitly in your project if needed.

> ⚠️ **Warning**: Apache Log4j 1.x has reached end-of-life. Consider using Log4j 2.x or Logback for new projects.

## Since Version

`@since 1.0.0`
