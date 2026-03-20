# LoggingExample

## Overview

`LoggingExample` is a demonstration class showing how to use the Microsphere Logging abstraction. It illustrates loading a `Logging` implementation via SPI and performing basic logging management operations.

## Details

| Property | Value |
|----------|-------|
| **Full Qualified Name** | `io.microsphere.logging.examples.LoggingExample` |
| **Type** | Class |
| **Module** | `microsphere-logging-examples` |
| **Package** | `io.microsphere.logging.examples` |
| **Since** | 1.0.0 |
| **Author** | [Mercy](mailto:mercyblitz@gmail.com) |

## Explanation

The `LoggingExample` class serves as a quick-start guide and integration test for the Microsphere Logging framework. It demonstrates:

1. **SPI-based loading**: Using `LoggingUtils.load()` to automatically discover and load the highest-priority `Logging` implementation.
2. **Logger creation**: Using the Microsphere `LoggerFactory.getLogger()` to create a framework-agnostic logger.
3. **Logging management**: Querying the active framework name and listing all registered loggers.

### Source Code

```java
package io.microsphere.logging.examples;

import io.microsphere.logging.Logger;
import io.microsphere.logging.Logging;

import static io.microsphere.logging.LoggerFactory.getLogger;
import static io.microsphere.logging.LoggingUtils.load;

public class LoggingExample {

    private static final Logger logger = getLogger(LoggingExample.class);

    public static void main(String[] args) {
        // The highest priority Logging instance will be loaded
        Logging logging = load();
        logger.info("The name of Logging : {}", logging.getName());

        // The all logger names of Logging
        logger.info("All logger names of Logging : {}", logging.getLoggerNames());
    }
}
```

### Expected Output

When run with all framework adapters on the classpath, the output will depend on which adapter has the highest priority:

```
[INFO] The name of Logging : Java Logging
[INFO] All logger names of Logging : [global, ...]
```

If only Logback is on the classpath:
```
[INFO] The name of Logging : Logback
[INFO] All logger names of Logging : [ROOT, com.example.myapp, ...]
```

## Example Code

### Running the Example

```bash
# From the project root
cd microsphere-logging-examples
mvn exec:java -Dexec.mainClass="io.microsphere.logging.examples.LoggingExample"
```

### Extending the Example

```java
import io.microsphere.logging.Logger;
import io.microsphere.logging.Logging;
import io.microsphere.logging.LoggingUtils;
import io.microsphere.logging.jmx.LoggingMXBeanRegistrar;

import static io.microsphere.logging.LoggerFactory.getLogger;

public class ExtendedLoggingExample {

    private static final Logger logger = getLogger(ExtendedLoggingExample.class);

    public static void main(String[] args) {
        // Load and display all available frameworks
        for (Logging logging : LoggingUtils.loadAll()) {
            logger.info("Available framework: {} (priority: {})",
                logging.getName(), logging.getPriority());
        }

        // Load the default (highest priority) framework
        Logging logging = LoggingUtils.load();
        logger.info("Active framework: {}", logging.getName());

        // Display supported levels
        logger.info("Supported levels: {}", logging.getSupportedLoggingLevels());

        // Manage log levels
        logging.setLoggerLevel("com.example", "DEBUG");
        logger.info("com.example level: {}", logging.getLoggerLevel("com.example"));

        // Register all frameworks as JMX MBeans
        LoggingMXBeanRegistrar.registerAll();
        logger.info("JMX MBeans registered");
    }
}
```

## Version Compatibility

| Version | Java | Notes |
|---------|------|-------|
| 1.0.0+ | 8+ | Requires at least one framework adapter on classpath |

### Maven Dependencies

The examples module includes all framework adapters:

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-logging-examples</artifactId>
    <version>0.1.1-SNAPSHOT</version>
</dependency>
```

## Since Version

`@since 1.0.0`
