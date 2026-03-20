# LoggingUtils

## Overview

`LoggingUtils` is a utility class that provides SPI-based loading of `Logging` implementations. It uses Java's `ServiceLoader` mechanism to discover and instantiate logging framework adapters at runtime, selecting them based on priority ordering.

## Details

| Property | Value |
|----------|-------|
| **Full Qualified Name** | `io.microsphere.logging.LoggingUtils` |
| **Type** | Abstract Class (utility) |
| **Module** | `microsphere-logging-commons` |
| **Package** | `io.microsphere.logging` |
| **Since** | 1.0.0 |
| **Author** | [Mercy](mailto:mercyblitz@gmail.com) |

## Explanation

`LoggingUtils` acts as the primary entry point for obtaining `Logging` instances. It abstracts away the `ServiceLoader` complexity and provides two modes of operation:

1. **Load All**: Retrieves all available `Logging` implementations sorted by priority.
2. **Load Single**: Retrieves only the highest-priority `Logging` implementation.

The class delegates to `io.microsphere.util.ServiceLoaderUtils` for the actual SPI loading, ensuring consistent behavior with other Microsphere projects.

### Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `loadAll()` | `List<Logging>` | Loads all `Logging` implementations using the default ClassLoader |
| `loadAll(ClassLoader)` | `List<Logging>` | Loads all `Logging` implementations using the specified ClassLoader |
| `load()` | `Logging` | Loads the highest-priority `Logging` implementation using the default ClassLoader |
| `load(ClassLoader)` | `Logging` | Loads the highest-priority `Logging` implementation using the specified ClassLoader |

### How SPI Discovery Works

1. The `ServiceLoader` scans `META-INF/services/io.microsphere.logging.Logging` files on the classpath.
2. Each file declares one fully-qualified class name per line (e.g., `io.microsphere.logging.jdk.JavaLogging`).
3. Implementations are instantiated and sorted by their `getPriority()` value.
4. `load()` returns the first (highest priority) implementation; `loadAll()` returns the full sorted list.

## Example Code

### Loading the Default Logging Implementation

```java
import io.microsphere.logging.Logging;
import io.microsphere.logging.LoggingUtils;

public class LoadDefaultExample {
    public static void main(String[] args) {
        // Load the highest-priority Logging implementation
        Logging logging = LoggingUtils.load();
        System.out.println("Active framework: " + logging.getName());
        // Output depends on classpath: "Java Logging", "Logback", "Log4j", or "Log4j2"
    }
}
```

### Loading All Available Logging Implementations

```java
import io.microsphere.logging.Logging;
import io.microsphere.logging.LoggingUtils;
import java.util.List;

public class LoadAllExample {
    public static void main(String[] args) {
        // Load all Logging implementations sorted by priority
        List<Logging> loggings = LoggingUtils.loadAll();

        for (Logging logging : loggings) {
            System.out.printf("Framework: %s (priority: %d)%n",
                logging.getName(), logging.getPriority());
        }
        // Example output:
        // Framework: Java Logging (priority: 20)
        // Framework: Logback (priority: 10)
        // Framework: Log4j (priority: 7)
        // Framework: Log4j2 (priority: 5)
    }
}
```

### Using a Custom ClassLoader

```java
import io.microsphere.logging.Logging;
import io.microsphere.logging.LoggingUtils;

public class CustomClassLoaderExample {
    public static void main(String[] args) {
        ClassLoader customLoader = Thread.currentThread().getContextClassLoader();
        Logging logging = LoggingUtils.load(customLoader);
        System.out.println("Loaded via custom ClassLoader: " + logging.getName());
    }
}
```

## Version Compatibility

| Version | Java | Notes |
|---------|------|-------|
| 1.0.0+ | 8+ | All methods available since initial release |

Dependencies:
- `microsphere-java` (0.1.9) for `ServiceLoaderUtils` and `ClassLoaderUtils`

## Since Version

`@since 1.0.0`
