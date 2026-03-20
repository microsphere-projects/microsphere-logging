# Logging Interface

## Overview

`Logging` is the core management interface for the logging facility in the Microsphere Logging framework. It provides a unified API for managing loggers and log levels across different logging frameworks (Java Logging, Log4j, Log4j 2, Logback). The interface methods are designed to be compatible with `java.util.logging.LoggingMXBean`.

## Details

| Property | Value |
|----------|-------|
| **Full Qualified Name** | `io.microsphere.logging.Logging` |
| **Type** | Interface |
| **Module** | `microsphere-logging-commons` |
| **Package** | `io.microsphere.logging` |
| **Extends** | `io.microsphere.lang.Prioritized` |
| **Since** | 1.0.0 |
| **Author** | [Mercy](mailto:mercyblitz@gmail.com) |

## Explanation

The `Logging` interface serves as the primary abstraction point for the Microsphere Logging project. It defines a standard contract that each logging framework adapter must implement. By extending `Prioritized`, implementations can be ranked and the highest-priority adapter is automatically selected via SPI when multiple frameworks are present on the classpath.

### Key Design Decisions

1. **JMX Compatibility**: Methods like `getLoggerLevel()`, `setLoggerLevel()`, and `getParentLoggerName()` mirror the `java.util.logging.LoggingMXBean` interface, enabling seamless JMX integration.
2. **Priority-Based Selection**: Through the `Prioritized` parent interface, each implementation declares its priority, allowing automatic framework selection.
3. **Default Method for Parent Logger**: The `getParentLoggerName()` method provides a default implementation that derives the parent logger name from the dot-separated package hierarchy.

### Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getRootLoggerName()` | `String` | Returns the name of the root logger (framework-specific) |
| `getLoggerNames()` | `List<String>` | Returns all currently registered logger names |
| `getSupportedLoggingLevels()` | `Set<String>` | Returns the set of supported log level names |
| `getLoggerLevel(String)` | `String` | Gets the log level for a specific logger |
| `setLoggerLevel(String, String)` | `void` | Sets the log level for a specific logger |
| `getParentLoggerName(String)` | `String` | Returns the parent logger name (has default implementation) |
| `getName()` | `String` | Returns the name of the logging framework |

## Example Code

### Loading and Using a Logging Instance

```java
import io.microsphere.logging.Logging;
import io.microsphere.logging.LoggingUtils;

public class LoggingDemo {
    public static void main(String[] args) {
        // Load the highest-priority Logging implementation via SPI
        Logging logging = LoggingUtils.load();

        // Display the logging framework name
        System.out.println("Active framework: " + logging.getName());
        // Output: "Active framework: Java Logging" (or "Logback", "Log4j", etc.)

        // Get the root logger name
        String rootLogger = logging.getRootLoggerName();
        System.out.println("Root logger: " + rootLogger);

        // List all registered loggers
        for (String loggerName : logging.getLoggerNames()) {
            System.out.println("Logger: " + loggerName);
        }

        // Get supported log levels
        System.out.println("Supported levels: " + logging.getSupportedLoggingLevels());

        // Read and modify a logger's level
        String currentLevel = logging.getLoggerLevel("com.example.myapp");
        System.out.println("Current level: " + currentLevel);

        logging.setLoggerLevel("com.example.myapp", "DEBUG");
        System.out.println("New level: " + logging.getLoggerLevel("com.example.myapp"));
    }
}
```

### Implementing the Logging Interface

```java
import io.microsphere.logging.Logging;
import java.util.*;

public class CustomLogging implements Logging {

    @Override
    public String getRootLoggerName() {
        return "ROOT";
    }

    @Override
    public List<String> getLoggerNames() {
        // Return all registered logger names from your custom framework
        return Arrays.asList("ROOT", "com.example.myapp");
    }

    @Override
    public Set<String> getSupportedLoggingLevels() {
        return new LinkedHashSet<>(Arrays.asList("OFF", "ERROR", "WARN", "INFO", "DEBUG", "TRACE", "ALL"));
    }

    @Override
    public String getLoggerLevel(String loggerName) {
        // Look up the level from your custom framework
        return "INFO";
    }

    @Override
    public void setLoggerLevel(String loggerName, String levelName) {
        // Set the level in your custom framework
    }

    @Override
    public String getName() {
        return "Custom Logging";
    }

    @Override
    public int getPriority() {
        return NORMAL_PRIORITY;
    }
}
```

Register it via SPI in `META-INF/services/io.microsphere.logging.Logging`:
```
com.example.CustomLogging
```

## Version Compatibility

| Version | Java | Notes |
|---------|------|-------|
| 1.0.0+ | 8+ | Core interface available since initial release |

The `Logging` interface depends on `io.microsphere.lang.Prioritized` from the `microsphere-java` library (version 0.1.9).

## Since Version

`@since 1.0.0`
