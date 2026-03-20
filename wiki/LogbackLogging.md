# LogbackLogging

## Overview

`LogbackLogging` is the `Logging` implementation for the Logback framework. It provides log level management through the Logback `LoggerContext` and related utilities.

## Details

| Property | Value |
|----------|-------|
| **Full Qualified Name** | `io.microsphere.logging.logback.LogbackLogging` |
| **Type** | Class |
| **Module** | `microsphere-logback` |
| **Package** | `io.microsphere.logging.logback` |
| **Implements** | `io.microsphere.logging.Logging` |
| **Priority** | `NORMAL_PRIORITY` (10) |
| **Since** | 1.0.0 |
| **Author** | [Mercy](mailto:mercyblitz@gmail.com) |

## Explanation

`LogbackLogging` adapts the Logback framework to the Microsphere `Logging` interface. It manages loggers through the Logback `LoggerContext` (obtained from the SLF4J factory) and delegates level operations to `io.microsphere.logging.logback.util.LoggerUtils`.

### Key Characteristics

| Characteristic | Value |
|---------------|-------|
| **Framework Name** | `"Logback"` |
| **Root Logger Name** | `"ROOT"` (from `org.slf4j.Logger.ROOT_LOGGER_NAME`) |
| **Supported Levels** | OFF, ERROR, WARN, INFO, DEBUG, TRACE, ALL |
| **Priority** | `NORMAL_PRIORITY` = 10 |

### Internal Delegation

| Method | Delegates To |
|--------|-------------|
| `getLoggerNames()` | `LoggerContext.getLoggerList()` — streams all loggers and maps to names |
| `getLoggerLevel(String)` | `LoggerUtils.getLevelString(String)` |
| `setLoggerLevel(String, String)` | `LoggerUtils.setLoggerLevel(String, String)` |

### SPI Registration

Registered in `META-INF/services/io.microsphere.logging.Logging`:
```
io.microsphere.logging.logback.LogbackLogging
```

### Level Resolution

Logback's `LoggerUtils.getLevel()` checks both the directly assigned level and the effective (inherited) level:

- If a logger has a directly set level, that level is returned.
- If no direct level is set (`null`), the effective level (inherited from ancestors) is returned.
- This provides accurate level reporting even for loggers relying on inheritance.

## Example Code

### Basic Usage

```java
import io.microsphere.logging.Logging;
import io.microsphere.logging.logback.LogbackLogging;

public class LogbackExample {
    public static void main(String[] args) {
        Logging logging = new LogbackLogging();

        System.out.println("Name: " + logging.getName());
        // Output: Name: Logback

        System.out.println("Root Logger: " + logging.getRootLoggerName());
        // Output: Root Logger: ROOT

        System.out.println("Supported Levels: " + logging.getSupportedLoggingLevels());
        // Output: [OFF, ERROR, WARN, INFO, DEBUG, TRACE, ALL]

        // Change a logger's level at runtime
        logging.setLoggerLevel("com.example.myapp", "DEBUG");
        System.out.println("Level: " + logging.getLoggerLevel("com.example.myapp"));
        // Output: Level: DEBUG

        // List all registered loggers
        for (String loggerName : logging.getLoggerNames()) {
            String level = logging.getLoggerLevel(loggerName);
            System.out.printf("Logger: %s -> %s%n", loggerName, level);
        }
    }
}
```

### Integration with Spring Boot

Logback is the default logging framework for Spring Boot. When using Microsphere Logging with Spring Boot:

```java
import io.microsphere.logging.Logging;
import io.microsphere.logging.LoggingUtils;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingConfig {

    @Bean
    public ApplicationRunner logLevelManager() {
        return args -> {
            Logging logging = LoggingUtils.load();
            // With Spring Boot, this will typically be Logback
            System.out.println("Active framework: " + logging.getName());

            // Dynamically adjust log levels
            logging.setLoggerLevel("org.springframework", "WARN");
            logging.setLoggerLevel("com.example", "DEBUG");
        };
    }
}
```

## Version Compatibility

| Version | Java | Logback Version | SLF4J Version | Notes |
|---------|------|----------------|---------------|-------|
| 1.0.0+ | 11+ | 1.5.32 | 2.0.17 | Default profile |
| 1.0.0+ | 8–10 | 1.2.12 | 1.7.36 | Java 8 profile |

### Maven Dependency

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-logback</artifactId>
    <version>0.1.1-SNAPSHOT</version>
</dependency>
```

**Note**: Logback (`ch.qos.logback:logback-classic`) and SLF4J (`org.slf4j:slf4j-api`) are declared as optional dependencies. You must include them explicitly in your project.

## Since Version

`@since 1.0.0`
