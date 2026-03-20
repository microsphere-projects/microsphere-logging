# LoggingMXBeanAdapter

## Overview

`LoggingMXBeanAdapter` adapts the Microsphere `Logging` interface to the standard `java.util.logging.LoggingMXBean` interface, enabling any `Logging` implementation to be exposed as a JMX MBean.

## Details

| Property | Value |
|----------|-------|
| **Full Qualified Name** | `io.microsphere.logging.jmx.LoggingMXBeanAdapter` |
| **Type** | Class |
| **Module** | `microsphere-logging-commons` |
| **Package** | `io.microsphere.logging.jmx` |
| **Implements** | `java.util.logging.LoggingMXBean`, `io.microsphere.wrapper.DelegatingWrapper` |
| **Since** | 1.0.0 |
| **Author** | [Mercy](mailto:mercyblitz@gmail.com) |

## Explanation

The `LoggingMXBeanAdapter` is an implementation of the **Adapter design pattern**. It wraps a `Logging` instance and delegates all `LoggingMXBean` method calls to the underlying `Logging` implementation. This allows any logging framework adapter (Log4j, Logback, etc.) to be seamlessly registered and managed through JMX.

### How It Works

1. Receives a `Logging` instance through its constructor.
2. Delegates `LoggingMXBean` interface methods (`getLoggerNames()`, `getLoggerLevel()`, `setLoggerLevel()`, `getParentLoggerName()`) to the wrapped `Logging` instance.
3. Implements `DelegatingWrapper` to expose the underlying `Logging` delegate via `getDelegate()`.

### Delegated Methods

| LoggingMXBean Method | Delegates To |
|---------------------|--------------|
| `getLoggerNames()` | `logging.getLoggerNames()` |
| `getLoggerLevel(String)` | `logging.getLoggerLevel(String)` |
| `setLoggerLevel(String, String)` | `logging.setLoggerLevel(String, String)` |
| `getParentLoggerName(String)` | `logging.getParentLoggerName(String)` |

## Example Code

### Creating an Adapter Manually

```java
import io.microsphere.logging.Logging;
import io.microsphere.logging.LoggingUtils;
import io.microsphere.logging.jmx.LoggingMXBeanAdapter;
import java.util.logging.LoggingMXBean;

public class AdapterExample {
    public static void main(String[] args) {
        // Load the default Logging implementation
        Logging logging = LoggingUtils.load();

        // Wrap it as a LoggingMXBean
        LoggingMXBean mxBean = new LoggingMXBeanAdapter(logging);

        // Use standard JMX interface methods
        System.out.println("Loggers: " + mxBean.getLoggerNames());
        System.out.println("Root level: " + mxBean.getLoggerLevel(""));
    }
}
```

### Registering with JMX MBeanServer

```java
import io.microsphere.logging.Logging;
import io.microsphere.logging.LoggingUtils;
import io.microsphere.logging.jmx.LoggingMXBeanAdapter;
import javax.management.*;
import java.lang.management.ManagementFactory;

public class JmxRegistrationExample {
    public static void main(String[] args) throws Exception {
        Logging logging = LoggingUtils.load();
        LoggingMXBeanAdapter adapter = new LoggingMXBeanAdapter(logging);

        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        ObjectName objectName = new ObjectName("io.microsphere.logging:type=" + logging.getName());
        mBeanServer.registerMBean(adapter, objectName);

        System.out.println("Registered MBean: " + objectName);
    }
}
```

> **Note**: For production usage, prefer `LoggingMXBeanRegistrar` which handles registration automatically.

## Version Compatibility

| Version | Java | Notes |
|---------|------|-------|
| 1.0.0+ | 8+ | Uses standard JMX APIs available in all Java 8+ runtimes |

## Since Version

`@since 1.0.0`
