# microsphere-logback

[中文版](./zh/logback.md)

> Adapter module for **Logback** (built on `logback-classic` / SLF4J).

---

## Version Information

| Item | Value |
|------|-------|
| Branch analyzed | `release` (document set `current`) |
| Artifact | `io.github.microsphere-projects:microsphere-logback` |
| Version on `release` branch | `0.2.3-SNAPSHOT` |
| Latest published version | `0.2.2` |
| Introduced since | `1.0.0` (0.1.x line) |

## Compatibility

| Requirement | Version |
|-------------|---------|
| Java | 8+ |
| Logback (`logback-classic`) | 1.2+ |
| SLF4J API | 1.7+ |
| microsphere-logging-commons | same version line (managed by the BOM) |

## Installation

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-logback</artifactId>
</dependency>
```

SPI registration: `META-INF/services/io.microsphere.logging.Logging` → `LogbackLogging`.

## Key Class: `io.microsphere.logging.logback.LogbackLogging`

Implements `Logging` on top of Logback's `LoggerContext`.

| Constant / Behavior | Value |
|---------------------|-------|
| Root logger name | `"ROOT"` (`org.slf4j.Logger.ROOT_LOGGER_NAME`) |
| `PRIORITY` | `NORMAL_PRIORITY` (0) — highest among the *third-party* backends |
| `ALL_LEVELS` | resolved from `ch.qos.logback.classic.Level`: `OFF`, `ERROR`, `WARN`, `INFO`, `DEBUG`, `TRACE`, `ALL` |
| `getName()` | `"Logback"` |

This module registers **only** the `Logging` service — it does **not** register a `LoggerFactory` SPI. Your logging
calls keep flowing through SLF4J/Logback exactly as before; this module contributes the *management* capability
(level query/change, logger listing, JMX).

## Usage Examples

Through the framework-agnostic `Logging` API:

```java
import io.microsphere.logging.Logging;
import static io.microsphere.logging.LoggingUtils.load;

Logging logging = load();                 // -> LogbackLogging when Logback is the winner
logging.setLoggerLevel("io.microsphere", "DEBUG");   // effective immediately
logging.setLoggerLevel("io.microsphere", null);      // inherit from the parent logger
```

Direct Logback manipulation through `LogbackUtils`:

```java
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import io.microsphere.logging.logback.util.LogbackUtils;

LoggerContext context = LogbackUtils.getLoggerContext();

Logger byClass = LogbackUtils.getLogger(MyService.class);
Logger byName = LogbackUtils.getLogger("io.microsphere");

Level level = LogbackUtils.getLevel("io.microsphere");              // the Level object
String levelString = LogbackUtils.getLevelString("io.microsphere"); // e.g. "DEBUG"

LogbackUtils.setLoggerLevel("io.microsphere", "TRACE");  // by name + level name
LogbackUtils.setLoggerLevel(byName, Level.DEBUG);        // by Logger + Level object
LogbackUtils.setLoggerLevel(byName, null);               // null => inherit effective level
```

JMX exposure (provided by commons):

```java
LoggingMXBeanRegistrar.registerAll();
// MBean ObjectName: io.microsphere.logging:type=Logback
```

## Notes

- The resolved level set is `OFF`, `ERROR`, `WARN`, `INFO`, `DEBUG`, `TRACE`, `ALL`. Logback's rarely-used
  `FINE`/`FINER`/`FINEST` aliases are not exposed by `getSupportedLoggingLevels()` because the resolver reads
  `public static final Level` fields only.
- `setLoggerLevel(loggerName, ...)` throws `IllegalArgumentException` for an unknown logger name or an invalid
  level name.
- `getLoggerNames()` reflects the `LoggerContext` inventory at call time; Logback creates loggers lazily on first
  `getLogger(...)`.
- Prefer uppercase level names for portability when the same management code may run against several backends.
