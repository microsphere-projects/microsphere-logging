# Logback LoggerUtils

## Overview

`LoggerUtils` is a utility class providing static methods for managing Logback loggers, including retrieval, level inspection, and level modification through the Logback `LoggerContext`.

## Details

| Property | Value |
|----------|-------|
| **Full Qualified Name** | `io.microsphere.logging.logback.util.LoggerUtils` |
| **Type** | Abstract Class (utility) |
| **Module** | `microsphere-logback` |
| **Package** | `io.microsphere.logging.logback.util` |
| **Since** | 1.0.0 |
| **Author** | [Mercy](mailto:mercyblitz@gmail.com) |

## Explanation

This utility class encapsulates all low-level operations for the Logback framework. It obtains the `LoggerContext` from the SLF4J `LoggerFactory` and provides methods for logger and level management.

### Special Level Resolution

Unlike other frameworks, the Logback `LoggerUtils.getLevel()` method has special handling:

1. First checks the **directly assigned level** on the logger.
2. If no direct level is set (`null`), returns the **effective level** (inherited from parent loggers).
3. This ensures accurate level reporting for loggers that rely on level inheritance.

### Constants

| Constant | Type | Description |
|----------|------|-------------|
| `loggerContext` | `LoggerContext` | The Logback `LoggerContext` obtained from `LoggerFactory.getILoggerFactory()` |

### Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getLoggerContext()` | `LoggerContext` | Returns the Logback `LoggerContext` |
| `getLogger(String name)` | `Logger` | Gets a logger by name |
| `getLogger(Class<?> cls)` | `Logger` | Gets a logger by class |
| `getLevel(String loggerName)` | `Level` | Gets the level for a named logger (checks both direct and effective) |
| `getLevel(Logger logger)` | `Level` | Gets the level for a logger instance (checks both direct and effective) |
| `setLoggerLevel(String loggerName, String levelName)` | `void` | Sets the level for a named logger |
| `setLoggerLevel(Logger logger, Level level)` | `void` | Sets the level for a logger instance |
| `getLevelString(String loggerName)` | `String` | Gets the level as a string for a named logger |
| `getLevelString(Level level)` | `String` | Converts a Level to its string representation |

## Example Code

```java
import io.microsphere.logging.logback.util.LoggerUtils;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

public class LogbackUtilsExample {
    public static void main(String[] args) {
        // Get the LoggerContext
        LoggerContext context = LoggerUtils.getLoggerContext();

        // Get a logger
        Logger logger = LoggerUtils.getLogger("com.example.myapp");

        // Get current level (returns effective level if no direct level is set)
        Level level = LoggerUtils.getLevel("com.example.myapp");
        System.out.println("Current level: " + level);

        // Set level at runtime
        LoggerUtils.setLoggerLevel("com.example.myapp", "DEBUG");

        // Verify the change
        String levelStr = LoggerUtils.getLevelString("com.example.myapp");
        System.out.println("Level string: " + levelStr);
        // Output: Level string: DEBUG

        // List all loggers with their levels
        for (Logger l : context.getLoggerList()) {
            System.out.printf("Logger: %s -> %s%n",
                l.getName(), LoggerUtils.getLevelString(LoggerUtils.getLevel(l)));
        }
    }
}
```

## Version Compatibility

| Version | Java | Logback Version | SLF4J Version | Notes |
|---------|------|----------------|---------------|-------|
| 1.0.0+ | 11+ | 1.5.32 | 2.0.17 | Default profile |
| 1.0.0+ | 8–10 | 1.2.12 | 1.7.36 | Java 8 profile |

## Since Version

`@since 1.0.0`
