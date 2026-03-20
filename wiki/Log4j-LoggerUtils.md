# Log4j LoggerUtils

## Overview

`LoggerUtils` is a utility class providing static methods for managing Log4j 1.x loggers, including retrieval, level inspection, and level modification.

## Details

| Property | Value |
|----------|-------|
| **Full Qualified Name** | `io.microsphere.logging.log4j.util.LoggerUtils` |
| **Type** | Abstract Class (utility) |
| **Module** | `microsphere-log4j` |
| **Package** | `io.microsphere.logging.log4j.util` |
| **Since** | 1.0.0 |
| **Author** | [Mercy](mailto:mercyblitz@gmail.com) |

## Explanation

This utility class encapsulates all low-level operations for the Log4j 1.x `Logger` and `LoggerRepository`. It provides a consistent API for retrieving loggers, querying their levels, and modifying levels at runtime.

### Constants

| Constant | Type | Value | Description |
|----------|------|-------|-------------|
| `ROOT_LOGGER_NAME` | `String` | `"root"` | The name of the Log4j root logger |
| `loggerRepository` | `LoggerRepository` | — | The Log4j `LoggerRepository` instance |

### Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `getLoggerRepository()` | `LoggerRepository` | Returns the Log4j `LoggerRepository` |
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
import io.microsphere.logging.log4j.util.LoggerUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;

public class Log4jUtilsExample {
    public static void main(String[] args) {
        // Get a logger
        Logger logger = LoggerUtils.getLogger("com.example.myapp");

        // Get current level
        Level level = LoggerUtils.getLevel("com.example.myapp");
        System.out.println("Current level: " + level);

        // Set level
        LoggerUtils.setLoggerLevel("com.example.myapp", "DEBUG");

        // Get level as string
        String levelStr = LoggerUtils.getLevelString("com.example.myapp");
        System.out.println("Level string: " + levelStr);
        // Output: Level string: DEBUG
    }
}
```

## Version Compatibility

| Version | Java | Log4j Version | Notes |
|---------|------|--------------|-------|
| 1.0.0+ | 8+ | 1.2.17 | Requires `log4j:log4j` on classpath |

## Since Version

`@since 1.0.0`
