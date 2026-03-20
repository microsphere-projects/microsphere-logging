# Log4j2Logging

## Overview

`Log4j2Logging` is the `Logging` implementation for the Apache Log4j 2.x framework. It provides log level management through the Log4j 2 `LoggerContext` and related utilities.

## Details

| Property | Value |
|----------|-------|
| **Full Qualified Name** | `io.microsphere.logging.log4j2.Log4j2Logging` |
| **Type** | Class |
| **Module** | `microsphere-log4j2` |
| **Package** | `io.microsphere.logging.log4j2` |
| **Implements** | `io.microsphere.logging.Logging` |
| **Priority** | `NORMAL_PRIORITY - 5` (5) |
| **Since** | 1.0.0 |
| **Author** | [Mercy](mailto:mercyblitz@gmail.com) |

## Explanation

`Log4j2Logging` adapts the modern Apache Log4j 2.x framework to the Microsphere `Logging` interface. It manages loggers through the Log4j 2 `LoggerContext` and delegates level operations to `io.microsphere.logging.log4j2.util.LoggerUtils`.

### Key Characteristics

| Characteristic | Value |
|---------------|-------|
| **Framework Name** | `"Log4j2"` |
| **Root Logger Name** | `""` (empty string, from `LogManager.ROOT_LOGGER_NAME`) |
| **Supported Levels** | OFF, FATAL, ERROR, WARN, INFO, DEBUG, TRACE, ALL |
| **Priority** | `NORMAL_PRIORITY - 5` = 5 |

### Internal Delegation

| Method | Delegates To |
|--------|-------------|
| `getLoggerNames()` | `LoggerContext.getLoggers()` — streams all loggers and maps to names |
| `getLoggerLevel(String)` | `LoggerUtils.getLevelString(String)` |
| `setLoggerLevel(String, String)` | `LoggerUtils.setLoggerLevel(String, String)` |

### SPI Registration

Registered in `META-INF/services/io.microsphere.logging.Logging`:
```
io.microsphere.logging.log4j2.Log4j2Logging
```

### Related Components

- **`Log4j2Logger`** — A package-private adapter wrapping `org.apache.logging.log4j.Logger` that extends `AbstractLogger` and implements `DelegatingWrapper`. Provides `trace`, `debug`, `info`, `warn`, `error` methods with enabled checks.
- **`Log4j2LoggerFactory`** — Factory class extending `LoggerFactory` that creates `Log4j2Logger` instances. Registered via SPI at `META-INF/services/io.microsphere.logging.LoggerFactory`.
- **`LoggerUtils`** — Utility class providing static methods for logger retrieval and level management (see [Log4j2 LoggerUtils](Log4j2-LoggerUtils)).

## Example Code

### Basic Usage

```java
import io.microsphere.logging.Logging;
import io.microsphere.logging.log4j2.Log4j2Logging;

public class Log4j2Example {
    public static void main(String[] args) {
        Logging logging = new Log4j2Logging();

        System.out.println("Name: " + logging.getName());
        // Output: Name: Log4j2

        System.out.println("Root Logger: '" + logging.getRootLoggerName() + "'");
        // Output: Root Logger: ''  (empty string)

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

public class Log4j2DiscoveryExample {
    public static void main(String[] args) {
        List<Logging> all = LoggingUtils.loadAll();
        for (Logging logging : all) {
            if ("Log4j2".equals(logging.getName())) {
                System.out.println("Log4j2 is available with priority: " + logging.getPriority());
            }
        }
    }
}
```

## Version Compatibility

| Version | Java | Log4j 2 Version | Notes |
|---------|------|-----------------|-------|
| 1.0.0+ | 8+ | 2.25.3 | Log4j 2 dependencies (`log4j-api`, `log4j-core`) are optional |

### Maven Dependency

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-log4j2</artifactId>
    <version>0.1.1-SNAPSHOT</version>
</dependency>
```

**Note**: Log4j 2 dependencies (`org.apache.logging.log4j:log4j-api` and `org.apache.logging.log4j:log4j-core`) are declared as optional. You must include them explicitly in your project.

## Since Version

`@since 1.0.0`
