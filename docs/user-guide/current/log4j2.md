# microsphere-log4j2

[中文版](./zh/log4j2.md)

> Adapter module for **Apache Log4j 2** (`log4j-api` / `log4j-core`), including appender and layout utilities.

---

## Version Information

| Item | Value |
|------|-------|
| Branch analyzed | `release` (document set `current`) |
| Artifact | `io.github.microsphere-projects:microsphere-log4j2` |
| Version on `release` branch | `0.2.3-SNAPSHOT` |
| Latest published version | `0.2.2` |
| Introduced since | `1.0.0` (0.1.x line) |

## Compatibility

| Requirement | Version |
|-------------|---------|
| Java | 8+ |
| Log4j2 (`log4j-api` + `log4j-core`) | 2.4+ |
| microsphere-logging-commons | same version line (managed by the BOM) |

## Installation

```xml
<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-log4j2</artifactId>
</dependency>
```

## SPI Registrations

| Service file | Implementation | Effect |
|--------------|----------------|--------|
| `META-INF/services/io.microsphere.logging.Logging` | `Log4j2Logging` | runtime management of Log4j2 loggers |
| `META-INF/services/io.microsphere.logging.LoggerFactory` | `Log4j2LoggerFactory` | `LoggerFactory.getLogger(...)` yields Log4j2-backed `Logger` instances |

## Key Classes

### `io.microsphere.logging.log4j2.Log4j2Logging`

| Constant / Behavior | Value |
|---------------------|-------|
| Root logger name | `""` (`org.apache.logging.log4j.LogManager.ROOT_LOGGER_NAME`) |
| `PRIORITY` | `NORMAL_PRIORITY - 5` (i.e. `-5`) — the lowest built-in backend priority |
| `ALL_LEVELS` | resolved from `org.apache.logging.log4j.Level`: `OFF`, `FATAL`, `ERROR`, `WARN`, `INFO`, `DEBUG`, `TRACE`, `ALL` |
| `getName()` | `"Log4j2"` |

### `io.microsphere.logging.log4j2.Log4j2Logger` / `Log4j2LoggerFactory`

Bridge `org.apache.logging.log4j.Logger` into the unified `io.microsphere.logging.Logger` facade:

```java
import io.microsphere.logging.Logger;
import io.microsphere.logging.LoggerFactory;

Logger logger = LoggerFactory.getLogger(MyClass.class); // Log4j2Logger on this classpath
logger.warn("Value: {}", value);
```

### `io.microsphere.logging.log4j2.util.Log4j2Utils`

Level management:

```java
import io.microsphere.logging.log4j2.util.Log4j2Utils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Configuration;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;

Configuration config = Log4j2Utils.getConfiguration();
LoggerContext context = Log4j2Utils.getLoggerContext();
Collection<Logger> loggers = Log4j2Utils.getLoggers();

Logger logger = Log4j2Utils.getLogger("io.microsphere");   // or getLogger(MyClass.class)

Level level = Log4j2Utils.getLevel("io.microsphere");
String levelString = Log4j2Utils.getLevelString("io.microsphere"); // e.g. "INFO"

Log4j2Utils.setLoggerLevel("io.microsphere", "DEBUG");   // by name
Log4j2Utils.setLoggerLevel(logger, Level.TRACE);         // by Logger + Level
Log4j2Utils.setLoggerLevel(logger, null);                // null => inherit from parent config
```

Appender manipulation:

```java
// Apply a consumer to every configured logger
Log4j2Utils.doInLogger(l -> l.setAdditivity(true));

// Attach / detach appenders
Log4j2Utils.addAppender(myAppender, Log4j2Utils.getLoggers());
Log4j2Utils.addAppenderForAllLoggers(myAppender);
Log4j2Utils.removeAppender(myAppender, loggers);
Log4j2Utils.removeAppenderForAllLoggers(myAppender);

// Look up a configured appender by name
ConsoleAppender console = Log4j2Utils.findAppender("Console");
```

### `io.microsphere.logging.log4j2.appender.InMemoryAppender`

An `Appender` (name `"InMemory"`, extends `AbstractLifeCycle`) that buffers `LogEvent`s in a
`ConcurrentSkipListSet` ordered by `LogEventComparator`, so captured events stay chronologically sorted without
manual sorting.

```java
InMemoryAppender appender = new InMemoryAppender();
appender.initialize();
appender.start();                       // lifecycle: required before recording

Log4j2Utils.addAppenderForAllLoggers(appender);
MyService.doSomething();                // code under test logs

// The buffer has no public read accessor; drain it into another appender to inspect,
// e.g. Log4j2's own ListAppender, then assert on that list.
appender.transfer(listAppender);

appender.stop();                        // stop() clears the buffered events

// Find the appender already declared in the Log4j2 configuration (may return null)
InMemoryAppender configured = InMemoryAppender.findInMemoryAppender();
```

### Layouts and comparator

- `DelegatingLayout<T extends Serializable>` — a `Layout` that forwards `toByteArray` / `toSerializable` /
  `encode` / header & footer to a delegate layout (construct with `new DelegatingLayout<>(other)` or the no-arg
  constructor). Useful when wrapping an appender without rewriting its formatting rules.
- `SmartFileAppenderLayout<T extends Serializable>` — constructed with a `LoggerContext`; for each `LogEvent` it
  selects the file appender/layout associated with the event's logger and formats accordingly. Its
  `DEFAULT_LAYOUT` is a `PatternLayout` with pattern `%m` (message only).
- `LogEventComparator` (singleton `INSTANCE`) — orders `LogEvent`s chronologically; already used internally by
  `InMemoryAppender`.

## Notes

- Level changes made through this adapter apply immediately to the running `LoggerContext`.
- With the JUL adapter also present, `LoggingUtils.load()` still returns `JavaLogging` first
  (priority `+10` vs `-5`); use `loadAll()` and filter by `getName()` to target Log4j2 explicitly.
- `FATAL` is available here and in Log4j 1.x, but not in Logback.
