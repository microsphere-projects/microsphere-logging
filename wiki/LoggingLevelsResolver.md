# LoggingLevelsResolver & DefaultLoggingLevelsResolver

## Overview

`LoggingLevelsResolver` is an interface for resolving all available log level names from a logging framework's level class. `DefaultLoggingLevelsResolver` is its default implementation that uses Java reflection to extract static final fields of the level type.

## Details

### LoggingLevelsResolver (Interface)

| Property | Value |
|----------|-------|
| **Full Qualified Name** | `io.microsphere.logging.LoggingLevelsResolver` |
| **Type** | Interface |
| **Module** | `microsphere-logging-commons` |
| **Package** | `io.microsphere.logging` |
| **Since** | 1.0.0 |
| **Author** | [Mercy](mailto:mercyblitz@gmail.com) |

### DefaultLoggingLevelsResolver (Implementation)

| Property | Value |
|----------|-------|
| **Full Qualified Name** | `io.microsphere.logging.DefaultLoggingLevelsResolver` |
| **Type** | Class |
| **Module** | `microsphere-logging-commons` |
| **Package** | `io.microsphere.logging` |
| **Implements** | `LoggingLevelsResolver` |
| **Since** | 1.0.0 |
| **Author** | [Mercy](mailto:mercyblitz@gmail.com) |

## Explanation

Logging frameworks define their available levels as static final fields in a level class (e.g., `java.util.logging.Level.INFO`, `org.apache.log4j.Level.DEBUG`). The `LoggingLevelsResolver` provides a generic way to discover these levels at runtime.

### How It Works

The `DefaultLoggingLevelsResolver` uses the following approach:

1. Takes a `Class<?>` parameter representing the logging framework's level class.
2. Uses reflection to scan all declared fields of that class.
3. Filters fields that are `static final` and whose type matches the level class itself.
4. Extracts the field names as the level names.
5. Returns an unmodifiable `Set<String>` of level names.

### Singleton Pattern

`DefaultLoggingLevelsResolver` provides a singleton via the `INSTANCE` field, which is used throughout all framework adapters.

### Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `resolve(Class<?>)` | `Set<String>` | Resolves all log level names from static final fields of the given class |

## Example Code

### Resolving Levels from Different Frameworks

```java
import io.microsphere.logging.DefaultLoggingLevelsResolver;
import java.util.Set;

public class LevelResolverExample {
    public static void main(String[] args) {
        DefaultLoggingLevelsResolver resolver = DefaultLoggingLevelsResolver.INSTANCE;

        // Resolve Java Logging levels
        Set<String> jdkLevels = resolver.resolve(java.util.logging.Level.class);
        System.out.println("JDK Levels: " + jdkLevels);
        // Output: [OFF, SEVERE, WARNING, INFO, CONFIG, FINE, FINER, FINEST, ALL]

        // Resolve Log4j levels (if on classpath)
        // Set<String> log4jLevels = resolver.resolve(org.apache.log4j.Level.class);
        // Output: [OFF, FATAL, ERROR, WARN, INFO, DEBUG, TRACE, ALL]

        // Resolve Log4j2 levels (if on classpath)
        // Set<String> log4j2Levels = resolver.resolve(org.apache.logging.log4j.Level.class);
        // Output: [OFF, FATAL, ERROR, WARN, INFO, DEBUG, TRACE, ALL]

        // Resolve Logback levels (if on classpath)
        // Set<String> logbackLevels = resolver.resolve(ch.qos.logback.classic.Level.class);
        // Output: [OFF, ERROR, WARN, INFO, DEBUG, TRACE, ALL]
    }
}
```

### Using the Interface for Custom Implementations

```java
import io.microsphere.logging.LoggingLevelsResolver;
import java.util.*;

public class CustomLevelResolver implements LoggingLevelsResolver {

    @Override
    public Set<String> resolve(Class<?> levelClass) {
        // Custom level resolution logic
        return new LinkedHashSet<>(Arrays.asList("FATAL", "ERROR", "WARN", "INFO", "DEBUG"));
    }
}
```

## Version Compatibility

| Version | Java | Notes |
|---------|------|-------|
| 1.0.0+ | 8+ | Uses reflection, compatible with all Java 8+ runtimes |

## Since Version

`@since 1.0.0`
