# LoggingMXBeanRegistrar

## Overview

`LoggingMXBeanRegistrar` is a utility class that registers all SPI-discovered `Logging` implementations as JMX MBeans in the Platform MBeanServer. This enables runtime monitoring and management of log levels across multiple logging frameworks via JMX tools (e.g., JConsole, VisualVM).

## Details

| Property | Value |
|----------|-------|
| **Full Qualified Name** | `io.microsphere.logging.jmx.LoggingMXBeanRegistrar` |
| **Type** | Abstract Class (utility) |
| **Module** | `microsphere-logging-commons` |
| **Package** | `io.microsphere.logging.jmx` |
| **Since** | 1.0.0 |
| **Author** | [Mercy](mailto:mercyblitz@gmail.com) |

## Explanation

The `LoggingMXBeanRegistrar` serves as the bridge between the Microsphere Logging SPI system and JMX. It automates the process of:

1. Loading all `Logging` implementations via `LoggingUtils.loadAll()`.
2. Wrapping each implementation with `LoggingMXBeanAdapter`.
3. Registering each adapter in the Platform MBeanServer with a unique `ObjectName`.

### JMX ObjectName Format

All registered MBeans follow the naming convention:

```
io.microsphere.logging:type={framework-name}
```

For example:
- `io.microsphere.logging:type=Java Logging`
- `io.microsphere.logging:type=Logback`
- `io.microsphere.logging:type=Log4j`
- `io.microsphere.logging:type=Log4j2`

### Constants

| Constant | Value | Description |
|----------|-------|-------------|
| `JMX_DOMAIN` | `"io.microsphere.logging"` | The JMX domain for all registered MBeans |

### Methods

| Method | Return Type | Description |
|--------|-------------|-------------|
| `registerAll()` | `List<ObjectInstance>` | Registers all SPI-loaded `Logging` instances using the default ClassLoader |
| `registerAll(ClassLoader)` | `List<ObjectInstance>` | Registers all SPI-loaded `Logging` instances using the specified ClassLoader |
| `register(Logging)` | `ObjectInstance` | Registers a single `Logging` instance; returns `null` on failure |
| `getRegisteredLoggingObjectInstances()` | `Set<ObjectInstance>` | Queries the MBeanServer for all registered Logging MBeans |

### Behavior Notes

- If an MBean with the same `ObjectName` is already registered, the method logs a warning and returns the existing instance.
- Registration failures are caught and logged; they do not throw exceptions.
- The returned `List<ObjectInstance>` is unmodifiable.

## Example Code

### Register All Logging Frameworks

```java
import io.microsphere.logging.jmx.LoggingMXBeanRegistrar;
import javax.management.ObjectInstance;
import java.util.List;

public class JmxSetupExample {
    public static void main(String[] args) {
        // Register all available Logging implementations as JMX MBeans
        List<ObjectInstance> instances = LoggingMXBeanRegistrar.registerAll();

        for (ObjectInstance instance : instances) {
            System.out.println("Registered: " + instance.getObjectName());
        }
        // Output:
        // Registered: io.microsphere.logging:type=Java Logging
        // Registered: io.microsphere.logging:type=Logback
        // (etc., depending on classpath)
    }
}
```

### Query Registered MBeans

```java
import io.microsphere.logging.jmx.LoggingMXBeanRegistrar;
import javax.management.ObjectInstance;
import java.util.Set;

public class QueryMBeansExample {
    public static void main(String[] args) {
        // First, register all
        LoggingMXBeanRegistrar.registerAll();

        // Then query what was registered
        Set<ObjectInstance> registered = LoggingMXBeanRegistrar.getRegisteredLoggingObjectInstances();
        System.out.println("Total registered MBeans: " + registered.size());
        for (ObjectInstance instance : registered) {
            System.out.println("  - " + instance.getObjectName());
        }
    }
}
```

### Managing Log Levels via JConsole

Once MBeans are registered, you can use JConsole or VisualVM to:

1. Navigate to the `io.microsphere.logging` domain.
2. Select a logging framework (e.g., "Logback").
3. Invoke `getLoggerLevel("com.example.myapp")` to read the current level.
4. Invoke `setLoggerLevel("com.example.myapp", "DEBUG")` to change the level at runtime.

## Version Compatibility

| Version | Java | Notes |
|---------|------|-------|
| 1.0.0+ | 8+ | Uses `java.lang.management.ManagementFactory` and `javax.management` APIs |

## Since Version

`@since 1.0.0`
