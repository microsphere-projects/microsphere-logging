# JavaLogging

## Overview

`JavaLogging` is the `Logging` implementation for Java's built-in logging framework (`java.util.logging`). It delegates all operations to the JDK's `LoggingMXBean` and has the highest priority among all framework adapters.

## Details

| Property | Value |
|----------|-------|
| **Full Qualified Name** | `io.microsphere.logging.jdk.JavaLogging` |
| **Type** | Class |
| **Module** | `microsphere-java-logging` |
| **Package** | `io.microsphere.logging.jdk` |
| **Implements** | `io.microsphere.logging.Logging` |
| **Priority** | `NORMAL_PRIORITY + 10` (highest) |
| **Since** | 1.0.0 |
| **Author** | [Mercy](mailto:mercyblitz@gmail.com) |

## Explanation

`JavaLogging` adapts the standard Java Logging (`java.util.logging`) framework to the Microsphere `Logging` interface. Since JDK logging is always available in any Java runtime (no external dependencies required), this implementation has the highest priority and will be selected by default unless overridden.

### Key Characteristics

| Characteristic | Value |
|---------------|-------|
| **Framework Name** | `"Java Logging"` |
| **Root Logger Name** | `""` (empty string) |
| **Supported Levels** | OFF, SEVERE, WARNING, INFO, CONFIG, FINE, FINER, FINEST, ALL |
| **Priority** | `NORMAL_PRIORITY + 10` = 20 |

### Internal Delegation

All operations delegate to `java.util.logging.LogManager.getLoggingMXBean()`:

| Method | Delegates To |
|--------|-------------|
| `getLoggerNames()` | `LoggingMXBean.getLoggerNames()` |
| `getLoggerLevel(String)` | `LoggingMXBean.getLoggerLevel(String)` |
| `setLoggerLevel(String, String)` | `LoggingMXBean.setLoggerLevel(String, String)` |
| `getParentLoggerName(String)` | `LoggingMXBean.getParentLoggerName(String)` |

### SPI Registration

Registered in `META-INF/services/io.microsphere.logging.Logging`:
```
io.microsphere.logging.jdk.JavaLogging
```

## Example Code

### Basic Usage

```java
import io.microsphere.logging.Logging;
import io.microsphere.logging.jdk.JavaLogging;

public class JavaLoggingExample {
    public static void main(String[] args) {
        // Direct instantiation
        Logging logging = new JavaLogging();

        System.out.println("Name: " + logging.getName());
        // Output: Name: Java Logging

        System.out.println("Root Logger: " + logging.getRootLoggerName());
        // Output: Root Logger: (empty string)

        System.out.println("Supported Levels: " + logging.getSupportedLoggingLevels());
        // Output: Supported Levels: [OFF, SEVERE, WARNING, INFO, CONFIG, FINE, FINER, FINEST, ALL]

        // Set a logger level
        logging.setLoggerLevel("com.example.myapp", "FINE");
        System.out.println("Level: " + logging.getLoggerLevel("com.example.myapp"));
        // Output: Level: FINE
    }
}
```

### Auto-Discovery via SPI

```java
import io.microsphere.logging.Logging;
import io.microsphere.logging.LoggingUtils;

public class AutoDiscoveryExample {
    public static void main(String[] args) {
        // If only microsphere-java-logging is on the classpath,
        // JavaLogging will be loaded automatically
        Logging logging = LoggingUtils.load();
        System.out.println(logging.getName()); // "Java Logging"
    }
}
```

## Version Compatibility

| Version | Java | Notes |
|---------|------|-------|
| 1.0.0+ | 8+ | Uses `java.util.logging` APIs available in all Java versions |

### Dependencies

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-java-logging</artifactId>
    <version>0.1.1-SNAPSHOT</version>
</dependency>
```

No external dependencies required — relies solely on the JDK.

## Since Version

`@since 1.0.0`
