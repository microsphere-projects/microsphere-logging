# Log4j2 LoggerUtils

## Overview

`LoggerUtils` is a utility class providing static methods for managing Log4j 2.x loggers, including retrieval, level inspection, and level modification through the `LoggerContext`.

## Details

| Property | Value |
|----------|-------|
| **Full Qualified Name** | `io.microsphere.logging.log4j2.util.LoggerUtils` |
| **Type** | Abstract Class (utility) |
| **Module** | `microsphere-log4j2` |
| **Package** | `io.microsphere.logging.log4j2.util` |
| **Since** | 1.0.0 |
| **Author** | [Mercy](mailto:mercyblitz@gmail.com) |

## Explanation

This utility class encapsulates all low-level operations for the Log4j 2.x framework. It uses the Log4j 2 `LoggerContext` to manage loggers and their configurations at runtime.

### Constants

| Constant | Type | Description |
|----------|------|-------------|
| `loggerContext` | `LoggerContext` | The Log4j 2 `LoggerContext` for managing logger configurations |

### Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getLoggerContext()` | `LoggerContext` | Returns the Log4j 2 `LoggerContext` |
| `getLogger(String name)` | `Logger` | Gets a logger by name |
| `getLogger(Class<?> cls)` | `Logger` | Gets a logger by class |
| `getLevel(String loggerName)` | `Level` | Gets the level for a named logger |
| `getLevel(Logger logger)` | `Level` | Gets the level for a logger instance |
| `setLoggerLevel(String loggerName, String levelName)` | `void` | Sets the level for a named logger |
| `setLoggerLevel(Logger logger, Level level)` | `void` | Sets the level for a logger instance |
| `getLevelString(String loggerName)` | `String` | Gets the level as a string for a named logger |
| `getLevelString(Level level)` | `String` | Converts a Level to its string representation |

## Example Code

```java
import io.microsphere.logging.log4j2.util.LoggerUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;

public class Log4j2UtilsExample {
    public static void main(String[] args) {
        // Get the LoggerContext
        LoggerContext context = LoggerUtils.getLoggerContext();
        System.out.println("Logger context: " + context.getName());

        // Get a logger
        Logger logger = LoggerUtils.getLogger("com.example.myapp");

        // Get current level
        Level level = LoggerUtils.getLevel("com.example.myapp");
        System.out.println("Current level: " + level);

        // Set level at runtime
        LoggerUtils.setLoggerLevel("com.example.myapp", "DEBUG");

        // Get level as string
        String levelStr = LoggerUtils.getLevelString("com.example.myapp");
        System.out.println("Level string: " + levelStr);
        // Output: Level string: DEBUG
    }
}
```

## Version Compatibility

| Version | Java | Log4j 2 Version | Notes |
|---------|------|-----------------|-------|
| 1.0.0+ | 8+ | 2.25.3 | Requires `log4j-api` and `log4j-core` on classpath |

## Since Version

`@since 1.0.0`
